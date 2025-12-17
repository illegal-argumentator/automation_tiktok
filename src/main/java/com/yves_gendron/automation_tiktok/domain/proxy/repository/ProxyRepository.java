package com.yves_gendron.automation_tiktok.domain.proxy.repository;

import com.yves_gendron.automation_tiktok.domain.proxy.model.Proxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProxyRepository extends JpaRepository<Proxy, String>, JpaSpecificationExecutor<Proxy> {

    boolean existsByUsernameAndPasswordAndHostAndPort(String username, String password, String host, int port);

}
