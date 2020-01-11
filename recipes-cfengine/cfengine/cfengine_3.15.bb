SUMMARY = "CFEngine is an IT infrastructure automation framework"
DESCRIPTION = "CFEngine is an IT infrastructure automation framework \
that helps engineers, system administrators and other stakeholders \
in an IT system to manage and understand IT infrastructure throughout \
its lifecycle. CFEngine takes systems from Build to Deploy, Manage and Audit."

HOMEPAGE = "http://cfengine.com"

SECTION = "utils"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=233aa25e53983237cf0bd4c238af255f"

inherit autotools-brokensep systemd

SRC_URI = " \
    gitsm://github.com/cfengine/core;protocol=https;branch=${PV}.x;name=core;destsuffix=core \
    ${@bb.utils.contains("PACKAGECONFIG", "enterprise", "git://github.com/cfengine/enterprise;protocol=https;branch=${PV}.x;name=enterprise;destsuffix=enterprise", "", d)} \
"
SRCREV_FORMAT="core_enterprise"
SRCREV_core = "${PV}.0"
SRCREV_enterprise = "${PV}.0"
DEPENDS = "lmdb bison-native"

S_core = "${WORKDIR}/core"
B_core = "${S_core}"
S_enterprise = "${WORKDIR}/enterprise"
B_enterprise = "${S_enterprise}"

S = "${S_core}"

do_configure_prepend() {
    NO_CONFIGURE=1 sh autogen.sh
}

addtask do_enterprise_configure after do_configure before do_compile

do_enterprise_configure() {
    if "${@bb.utils.contains('PACKAGECONFIG', 'enterprise', 'true', 'false', d)}"; then
        cd ${B_enterprise}
        NO_CONFIGURE=1 sh autogen.sh
        ./configure ${CONFIGUREOPTS} ${EXTRA_OECONF}
        cd -
    fi
}

addtask do_enterprise_compile after do_compile before do_install

do_enterprise_compile() {
    if "${@bb.utils.contains('PACKAGECONFIG', 'enterprise', 'true', 'false', d)}"; then
        cd ${B_enterprise}
        oe_runmake
        cd -
    fi
}

addtask do_enterprise_install after do_install before do_package

do_enterprise_install() {
    if "${@bb.utils.contains('PACKAGECONFIG', 'enterprise', 'true', 'false', d)}"; then
        cd ${B_enterprise}
        oe_runmake 'DESTDIR=${D}' install
        cd -
    fi
}

FILES_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'enterprise', '${libdir}/${PN}-enterprise${SOLIBSDEV}', '', d)}"

PACKAGECONFIG[libpcre] = "--with-pcre=yes,--with-pcre=no,libpcre,"
PACKAGECONFIG[openssl] = "--with-openssl=yes,--with-openssl=no,openssl,"
PACKAGECONFIG[pam] = "--with-pam=yes,--with-pam=no,libpam,"
PACKAGECONFIG[systemd] = "--with-systemd-service=${systemd_system_unitdir},--without-systemd-service"
PACKAGECONFIG[libcurl] = "--with-libcurl,--without-libcurl,curl,"
PACKAGECONFIG[enterprise] = ",,,"

PACKAGECONFIG ??= "libpcre openssl libcurl enterprise \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'pam systemd', d)} \
"

SYSTEMD_SERVICE_${PN} = "cfengine3.service cf-apache.service cf-hub.service cf-postgres.service \
                         cf-runalerts.service cf-execd.service \
                         cf-monitord.service  cf-serverd.service \
"
SYSTEMD_AUTO_ENABLE_${PN} = "disable"
