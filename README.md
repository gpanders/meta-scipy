meta-scipy
==========

This layer contains a recipe for building Scipy 1.3.3 as well as a series of
patches and `bbappend` files necessary to build the recipe.

Table of Contents
-----------------

- [Installation](#installation)
- [Contributing](#contributing)
- [Dependencies](#dependencies)
- [FORTRAN support](#fortran-support)
- [LAPACK](#lapack)
- [CMake](#cmake)
- [Numpy Distutils](#numpy-distutils)

Installation
------------

Clone this repository with tag or branch corresponding to your OpenEmbedded
version and add it to your workspace. For example, if using `warrior`:

    git clone -b warrior https://github.com/gpanders/meta-scipy meta-scipy
    bitbake-layers add-layer meta-scipy

Contributing
------------

This repository serves as a community hub for a full OpenEmbedded Scipy
solution. If you're able to get Scipy working for your platform or application,
please consider [contributing your changes][firstcontributions]. You can also
update the [wiki][], which is publicly editable.

Contributions should be well-tested and should adhere to the [OpenEmbedded
style guide][styleguide].

[firstcontributions]: https://firstcontributions.github.io/
[wiki]: https://github.com/gpanders/meta-scipy/wiki
[styleguide]: https://www.openembedded.org/wiki/Styleguide

Dependencies
------------

This layer depends on:

*  [meta-oe][]
*  [meta-python][]

The recipe was tested using the `warrior` branch in all upstream layers
(including the `poky` reference distribution). YMMV with other versions.

[meta-oe]: https://layers.openembedded.org/layerindex/branch/master/layer/meta-oe/
[meta-python]: https://layers.openembedded.org/layerindex/branch/master/layer/meta-python/

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
attempts to link against `cblas_` versions of BLAS methods). The `lapack` recipe
included in the [meta-oe][] layer is able to build the CBLAS library
by setting the `CBLAS=ON` CMake parameter. This is done in the
[`lapack_%.bbappend`][lapack] file.

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
