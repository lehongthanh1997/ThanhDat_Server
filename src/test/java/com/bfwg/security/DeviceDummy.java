package com.bfwg.security;


import org.springframework.stereotype.Component;

/**
 * Created by fanjin on 2017-10-07.
 */
@Component
public class DeviceDummy  {
    private boolean normal;
    private boolean mobile;
    private boolean tablet;

    public boolean isNormal() {
        return normal;
    }


    public boolean isMobile() {
        return mobile;
    }

    public boolean isTablet() {
        return tablet;
    }

//    @Override
//    public DevicePlatform getDevicePlatform() {
//        return null;
//    }

    public void setNormal(boolean normal) {
        this.normal = normal;
    }

//    public void setMobile(boolean mobile) {
//        this.mobile = mobile;
//    }

    public void setTablet(boolean tablet) {
        this.tablet = tablet;
    }
}
