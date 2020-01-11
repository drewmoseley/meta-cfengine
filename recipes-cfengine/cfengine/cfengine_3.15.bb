SUMMARY = "CFEngine is an IT infrastructure automation framework"

DESCRIPTION = "CFEngine is an IT infrastructure automation framework \
that helps engineers, system administrators and other stakeholders \
in an IT system to manage and understand IT infrastructure throughout \
its lifecycle. CFEngine takes systems from Build to Deploy, Manage and Audit."

HOMEPAGE = "http://cfengine.com"

SECTION = "utils"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=233aa25e53983237cf0bd4c238af255f"

inherit autotools systemd

SRC_URI = "gitsm://github.com/cfengine/core;protocol=https;branch=${PV}.x"
SRCREV = "${PV}.0"
DEPENDS = "lmdb bison-native"

S = "${WORKDIR}/git"
B = "${S}"

do_configure_prepend() {
    NO_CONFIGURE=1 sh ${B}/autogen.sh
}

PACKAGECONFIG[libpcre] = "--with-pcre=yes,--with-pcre=no,libpcre,"
PACKAGECONFIG[openssl] = "--with-openssl=yes,--with-openssl=no,openssl,"
PACKAGECONFIG[pam] = "--with-pam=yes,--with-pam=no,libpam,"
PACKAGECONFIG[systemd] = "--with-systemd-service=${systemd_system_unitdir},--without-systemd-service"
PACKAGECONFIG[libcurl] = "--with-libcurl,--without-libcurl,curl,"

PACKAGECONFIG ??= "libpcre openssl libcurl \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'pam systemd', d)} \
"

SYSTEMD_SERVICE_${PN} = "cfengine3.service cf-apache.service cf-hub.service cf-postgres.service \
                         cf-runalerts.service cf-execd.service \
                         cf-monitord.service  cf-serverd.service \
"
SYSTEMD_AUTO_ENABLE_${PN} = "disable"
