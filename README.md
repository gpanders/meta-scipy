OpenEmbedded recipe for scipy
=============================

This repository contains a recipe for building Scipy 1.3.3 in the OpenEmbedded
build system as well as a series of patches and `bbappend` files necessary to
build the recipe.

Installation
------------

There are a couple of ways to use this recipe in your own design:

1.  Simply clone this repo as a new layer in your workspace

        git clone https://github.com/gpanders/oe-scipy meta-scipy
        bitbake-layers add-layer meta-scipy

2.  Download the archive as a ZIP file and unzip the contents into your own
    layer

        wget https://github.com/gpanders/oe-scipy/archive/master.zip
        unzip master.zip
        cp -r oe-scipy-master/recipes-devtools meta-mylayer/

**Do not** add this recipe into an existing upstream layer (such as `meta-oe`).

FORTRAN support
---------------

To enable FORTRAN support, append the following lines to your `local.conf` file:

    FORTRAN_forcevariable = ",fortran"
    RUNTIMETARGET_append_pn-gcc-runtime = " libquadmath"

If you're using a custom distribution, you can alternatively include those two
lines in your `distro.conf` file.

LAPACK
------

By default, Scipy requires a CBLAS implementation of the BLAS library (it
attemps to link against `cblas_` versions of BLAS methods). The `lapack` recipe
included in the [meta-oe][] layer is able to build the CBLAS library
by setting the `CBLAS=ON` CMake parameter. This is done in the
[`lapack_%.bbappend`][lapack] file.

[meta-oe]: https://layers.openembedded.org/layerindex/branch/master/layer/meta-oe/
[lapack]: recipes-devtools/lapack/lapack_%25.bbappend

CMake
-----

Unfortunately, setting `CBLAS=ON` in the `lapack` recipe causes other errors
when building LAPACK. In particular, the LAPACK CMake build process has a step
to ensure that the provided Fortran and C compilers are compatible with each
other. This step had an unknown bug when performed in a cross-compiling
context which has now been addressed [upstream][]. The patch for that fix is
included in the [`cmake-native_%.bbappend`][cmake] file in this repository.

[upstream]: https://gitlab.kitware.com/cmake/cmake/merge_requests/4404
[cmake]: recipes-devtools/cmake/cmake-native_%25.bbappend

Numpy Distutils
---------------

The two above fixes allow Scipy to compile, but Yocto will still complain when
Scipy is packaged because the shared objects are linked using the `-rpath`
flag, which provides a runtime path to the binary to instruct the loader where
to look for other libraries at runtime. However, the paths provided are paths
on the build machine, not on the target machine, which Yocto (correctly)
detects and warns you about.

In our case, all of our libraries are in the standard library locations
(`/usr/lib`) on the target, so there is no need to include runtime path
information in the shared object files. Unfortunately, [there is no way to
inform `numpy` to not use runtime library paths when linking][rpath]. The patch
in [`python3-numpy_%.bbappend`][numpy] forces numpy to disable runtime paths when
cross-compiling libraries using `numpy.distutils`.

**NOTE** that this is _not_ really a good solution and is simply a hack. It is
possible that this patch _may_ break other recipes.

[rpath]: https://mail.python.org/pipermail/scipy-dev/2020-March/024058.html
[numpy]: recipes-devtools/python-numpy/python3-numpy_%25.bbappend
