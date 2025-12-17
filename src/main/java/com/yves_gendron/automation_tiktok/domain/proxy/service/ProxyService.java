package com.yves_gendron.automation_tiktok.domain.proxy.service;

import com.yves_gendron.automation_tiktok.config.AppProps;
import com.yves_gendron.automation_tiktok.domain.proxy.common.exception.ProxyAlreadyExistsException;
import com.yves_gendron.automation_tiktok.domain.proxy.common.exception.ProxyNotFoundException;
import com.yves_gendron.automation_tiktok.domain.proxy.common.mapper.ProxyMapper;
import com.yves_gendron.automation_tiktok.domain.proxy.model.Proxy;
import com.yves_gendron.automation_tiktok.domain.proxy.model.embedded.Geolocation;
import com.yves_gendron.automation_tiktok.domain.proxy.model.embedded.RotationData;
import com.yves_gendron.automation_tiktok.domain.proxy.repository.ProxyRepository;
import com.yves_gendron.automation_tiktok.domain.proxy.web.dto.AddProxyRequest;
import com.yves_gendron.automation_tiktok.domain.proxy.web.dto.ProxyFilterRequest;
import com.yves_gendron.automation_tiktok.domain.proxy.web.dto.UpdateProxyRequest;
import com.yves_gendron.automation_tiktok.system.client.ip_api.IpApiClient;
import com.yves_gendron.automation_tiktok.system.client.ip_api.common.dto.GetProxyAddressResponse;
import com.yves_gendron.automation_tiktok.system.client.ip_api.common.exception.IpApiException;
import jakarta.persistence.criteria.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProxyService {

    private final AppProps appProps;

    private final IpApiClient ipApiClient;

    private final ProxyMapper proxyMapper;

    private final ProxyRepository proxyRepository;

    private final ProxyVerifier proxyVerifier;

    public List<Proxy> findAll() {
        return proxyRepository.findAll();
    }

    public List<Proxy> findAllWithFilter(ProxyFilterRequest proxyFilterRequest) {
        Specification<Proxy> spec = Specification.unrestricted();

        if (proxyFilterRequest.getUsername() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("username")), proxyFilterRequest.getUsername().toLowerCase()));
        }

        if (proxyFilterRequest.getVerified() != null) {
            spec = spec.and((root, query, cb) -> {
                Path<Boolean> verifiedAttribute = root.get("verified");
                if (proxyFilterRequest.getVerified()) {
                    return cb.isTrue(verifiedAttribute);
                }
                return cb.isFalse(verifiedAttribute);
            });
        }

        if (proxyFilterRequest.getAccountsLinkedLessThan() != null) {
            spec = spec.and((root, query, cb) -> {
                Path<Integer> accountsLinked = root.get("accountsLinked");
                return cb.lessThan(accountsLinked, appProps.getAccountsPerProxy());
            });
        }

        return proxyRepository.findAll(spec);
    }

    public void saveAll(AddProxyRequest addProxyRequest) {
        List<Proxy> proxies = new ArrayList<>();

        for (AddProxyRequest.ProxyRequest proxyRequest : addProxyRequest.getProxies()) {
            Proxy proxy = proxyMapper.mapDtoToEntity(proxyRequest);

            throwIfProxyExists(proxy);
            proxyVerifier.verifyProxy(proxy);

            try {
                populateProxyWithGeolocationAndSetDefaults(proxy);
            } catch (IpApiException e) {
                log.warn("Skipping saving proxy {}, cause: {}", proxy.getUsername(), e.getMessage());
                continue;
            }

            proxies.add(proxy);
        }

        proxyRepository.saveAll(proxies);
    }

    private void populateProxyWithGeolocationAndSetDefaults(Proxy proxy) throws IpApiException {
        GetProxyAddressResponse proxyAddress = ipApiClient.getProxyAddress(proxy);

        Geolocation geolocation = Geolocation.builder()
                .countryCode(proxyAddress.getCountryCode())
                .timezone(proxyAddress.getTimezone())
                .build();

        RotationData rotationData = RotationData.builder()
                .autoRotateInterval(proxy.getRotationData().getAutoRotateInterval())
                .lastRotation(Instant.now())
                .autoRotationLink(proxy.getRotationData().getAutoRotationLink())
                .build();

        proxy.setGeolocation(geolocation);
        proxy.setAccountsLinked(0);
        proxy.setVerified(true);
        proxy.setRotationData(rotationData);
    }

    public void update(String id, UpdateProxyRequest updateProxyRequest) {
        Proxy proxy = findByIdOrThrow(id);

        Optional.ofNullable(updateProxyRequest.getVerified()).ifPresent(proxy::setVerified);
        Optional.ofNullable(updateProxyRequest.getUsername()).ifPresent(proxy::setUsername);
        Optional.ofNullable(updateProxyRequest.getPassword()).ifPresent(proxy::setPassword);
        Optional.ofNullable(updateProxyRequest.getHost()).ifPresent(proxy::setHost);
        Optional.ofNullable(updateProxyRequest.getCountryCode()).ifPresent(countryCode -> proxy.getGeolocation().setCountryCode(countryCode));
        Optional.ofNullable(updateProxyRequest.getPort()).ifPresent(proxy::setPort);
        Optional.ofNullable(updateProxyRequest.getAccountsLinked()).ifPresent(proxy::setAccountsLinked);
        Optional.ofNullable(updateProxyRequest.getLastRotation()).ifPresent(lastRotation -> proxy.getRotationData().setLastRotation(lastRotation));
        Optional.ofNullable(updateProxyRequest.getAutoRotateInterval()).ifPresent(autoRotateInterval -> proxy.getRotationData().setAutoRotateInterval(autoRotateInterval));
        Optional.ofNullable(updateProxyRequest.getAutoRotateInterval()).ifPresent(autoRotateInterval -> proxy.getRotationData().setAutoRotateInterval(autoRotateInterval));

        proxyRepository.saveAndFlush(proxy);
    }

    private Proxy findByIdOrThrow(String id) {
        return proxyRepository.findById(id)
                .orElseThrow(() -> new ProxyNotFoundException("Cannot find proxy by id " + id));
    }

    private void throwIfProxyExists(Proxy proxy) {
        boolean exists = proxyRepository.existsByUsernameAndPasswordAndHostAndPort(
                proxy.getUsername(), proxy.getPassword(), proxy.getHost(), proxy.getPort());

        if (exists) {
            throw new ProxyAlreadyExistsException("Proxy already exists: " + proxy.getUsername());
        }
    }
}
