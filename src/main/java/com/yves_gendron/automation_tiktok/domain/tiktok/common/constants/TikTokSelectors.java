package com.yves_gendron.automation_tiktok.domain.tiktok.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Month;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TikTokSelectors {
    public static String HOME_SIGN_UP_SPAN = "span:has-text('Sign up | TikTok')";
    public static String HOME_L0G_IN_SPAN = "span:has-text('Log in | TikTok')";
    public static String PLEASE_TRY_AGAIN_SPAN = "span:has-text('Something went wrong. Please try again later.')";
    public static String VERIFICATION_CODE_EXPIRED_SPAN = "span:has-text('Verification code is expired or incorrect. Try again.')";
    public static String PROFILE_HAS_BEED_UPDATED_SPAN = "span:has-text('Profile has been updated')";

    public static String USERNAME_NOT_AVAILABLE_CLASS = "div.tiktok-tfg9wk-DivTextContainer.e3v3zbj0";
    public static String SUGGESTED_CLASS = "tiktok-6d5fve-DivLabel e1ulspo1";

    public static String SIGN_UP_USE_PHONE_OR_EMAIL_DIV = "div[data-e2e='channel-item']:has-text('Use phone or email')";
    public static String MONTH_DIV = "div[role='combobox'][aria-label^='Month']";
    public static String DAY_DIV = "div[role='combobox'][aria-label^='Day']";
    public static String YEAR_DIV = "div[role='combobox'][aria-label^='Year']";
    public static String SELECT_ADD_DIV = "div.webapp-pa-prompt_container__pa_button";
    public static String COMMENT_TEXT_DIV = "div[data-e2e='comment-text'] div[contenteditable='true']";
    public static String POST_COMMENT_DIV = "div[data-e2e='comment-post']";
    public static String UPLOAD_DIV = "div.TUXButton-content:has-text('Upload')";
    public static String CONTINUE_WITH_FACEBOOK_DIV = "div[data-e2e='channel-item']:has-text('Continue with Facebook')";

    public static String APP_SUGGESTION_TEXT = "text=For security purposes, continue on the TikTok app";
    public static String MAXIMUM_ATTEMPTS_REACHED_TEXT = "text=Maximum number of attempts reached. Try again later.";
    public static String LOG_IN_USE_PHONE_OR_EMAIL_OR_USERNAME_TEXT = "text=Use phone / email / username";
    public static String SIGN_UP_WITH_EMAIL_TEXT = "text=Sign up with email";
    public static String LOG_IN_WITH_EMAIL_OR_USERNAME_TEXT = "text=Log in with email or username";
    public static String UPLOADED_TEXT = "text=Uploaded";
    public static String VIDEO_PUBLISHED_TEXT = "text=Video published";
    public static String LOGGED_IN_TEXT = "text=Logged in";
    public static String SKIP_TEXT = "text=Skip";
    public static String COMMENTS_TURNED_OFF_TEXT = "text=Comments are turned off";
    public static String ALREADY_LOGGED_IN_TEXT = "text=You're already logged in";

    public static String SIGN_UP_EMAIL_INPUT = "input[placeholder='Email address']";
    public static String LOG_IN_EMAIL_INPUT = "input[placeholder='Email or username']";
    public static String PASSWORD_INPUT = "input[placeholder='Password']";
    public static String CODE_INPUT = "input[placeholder='Enter 6-digit code']";
    public static String USERNAME_INPUT = "input[name='new-username']";
    public static String FILE_INPUT = "input[type='file']";

    public static String SEND_CODE_ENABLED_BUTTON = "button[data-e2e='send-code-button']:not([disabled])";
    public static String SEND_CODE_DISABLED_BUTTON = "button:has-text('Send code')[disabled]";
    public static String NEXT_BUTTON = "button:has-text('Next')";
    public static String SIGN_UP_BUTTON = "button:has-text('Sign up')";
    public static String LOG_IN_BUTTON = "button:has-text('Log in')";
    public static String NEXT_VIDEO_BUTTON = "button svg path[d^='m24 27.76']";
    public static String CANCEL_CONTENT_CHECKS_BUTTON = "button[data-type='neutral']:has-text('Cancel')";
    public static String POST_BUTTON = "button[data-e2e='post_video_button']";
    public static String POST_NOW_BUTTON = "button:has-text('Post now')";
    public static String EDIT_PROFILE_BUTTON = "button:has-text('Edit profile')";
    public static String PROFILE_BUTTON = "button:has-text('Profile')";
    public static String APPLY_BUTTON = "button:has-text('Apply')";
    public static String SAVE_BUTTON = "button:has-text('Save')";

    public static String SUSPICIOUS_ACTIVITY_DETECTED_H1 = "h1:has-text('Suspicious activity detected')";

    public static String SUGGESTED_ELEMENTS_UL = "ul.tiktok-3s5rn0-UlList li";

    public static String selectCommentButton(int videoIndex) {
        return "article[data-scroll-index='%d'] button[aria-label^='Read or add comments']".formatted(videoIndex);
    }
    public static String selectLikeButton(int videoIndex) {
        return "article[data-scroll-index='%d'] button[aria-label^='Like video']".formatted(videoIndex);
    }

    public static String CAPTCHA_ID = "#captcha-verify-container-main-page";

    public static String CAPTCHA_IMG = "img[alt='Captcha']";

    public static String CAPTCHA_SLIDEBAR_CLASS = ".cap-w-full.cap-h-40";
    public static String CAPTCHA_SLIDER_ICON_CLASS = ".secsdk-captcha-drag-icon";

    public static String LOADING_CIRCLE_SVG = "svg[class*='StyledLoadingCircle']";

    public static String selectLiveNow(int videoIndex) {
        return "article[data-scroll-index='%d']:has-text('LIVE now') button[aria-label^='Like video']".formatted(videoIndex);
    }
    public static String selectMonth(Month month) {
        return "div[role='option'] >> text=%s".formatted(month.name().charAt(0) + month.name().substring(1).toLowerCase());
    }
    public static String selectDay(int dayOfMonth) {
        return "div[role='option'] >> text=%s".formatted(dayOfMonth);
    }
    public static String selectYear(int year) {
        return "div[role='option'] >> text=%d".formatted(year);
    }
}
