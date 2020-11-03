meta-scipy
==========

This layer contains a recipe for building Scipy in the OpenEmbedded build
system, as well as a series of patches and `bbappend` files necessary to build
the recipe.

Table of Contents
-----------------

- [Dependencies](#dependencies)
- [Installation](#installation)
- [Contributing](#contributing)

Dependencies
------------

This layer depends on:

*  [meta-oe][]
*  [meta-python][]

[meta-oe]: https://layers.openembedded.org/layerindex/branch/master/layer/meta-oe/
[meta-python]: https://layers.openembedded.org/layerindex/branch/master/layer/meta-python/

Installation
------------

Clone this repository with the tag or branch corresponding to your OpenEmbedded
version and add it to your workspace. For example, if using `warrior`:

    git clone -b warrior https://github.com/gpanders/meta-scipy meta-scipy
    bitbake-layers add-layer meta-scipy

You will also need to enable FORTRAN support by adding the following to your
`local.conf` file:

    FORTRAN_forcevariable = ",fortran"
    RUNTIMETARGET_append_pn-gcc-runtime = " libquadmath"

If you're using a custom distribution, you can alternatively include the two
above lines in your `distro.conf` file.

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
