SUMMARY = "SciPy: Scientific Library for Python"
HOMEPAGE = "https://www.scipy.org"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=eb7262aea2504e4c0dfd16f5079e14dd"

SRC_URI = "https://files.pythonhosted.org/packages/a7/5c/495190b8c7cc71977c3d3fafe788d99d43eeb4740ac56856095df6a23fbd/scipy-1.3.3.tar.gz \
           file://0001-Allow-passing-flags-via-FARCH-for-mach.patch \
           "

SRC_URI[md5sum] = "b265efea6ce2f2c1e580cc66bfb8b117"
SRC_URI[sha256sum] = "64bf4e8ae0db2d42b58477817f648d81e77f0b381d0ea4427385bba3f959380a"

S = "${WORKDIR}/scipy-${PV}"

RDEPENDS_${PN} += "${PYTHON_PN}-numpy ${PYTHON_PN}-multiprocessing lapack"
DEPENDS += "${PYTHON_PN}-numpy ${PYTHON_PN}-numpy-native lapack"

CLEANBROKEN = "1"

inherit setuptools3

export LAPACK = "${STAGING_LIBDIR}"
export BLAS = "${STAGING_LIBDIR}"

export F90 = "${TARGET_PREFIX}gfortran"
export FARCH = "${TUNE_CCARGS}"
# Numpy expects the LDSHARED env variable to point to a single
# executable, but OE sets it to include some flags as well. So we split
# the existing LDSHARED variable into the base executable and flags, and
# prepend the flags into LDFLAGS
LDFLAGS_prepend := "${@" ".join(d.getVar('LDSHARED', True).split()[1:])} "
export LDSHARED := "${@d.getVar('LDSHARED', True).split()[0]}"

# Tell Numpy to look in target sysroot site-packages directory for libraries
LDFLAGS_append = " -L${STAGING_LIBDIR}/${PYTHON_DIR}/site-packages/numpy/core/lib"
