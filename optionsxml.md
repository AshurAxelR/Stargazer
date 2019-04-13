# Config XML Format

**File:** `config/options.xml`

## &lt;stargazer&gt;

#### Context

Root element.

#### Attributes

None.

#### Contents

[`<options>`](#options), [`<chartOptions>`](#chartoptions), [`<worlds>`](#worlds)


## &lt;options&gt;

The `<options>` element contains a set of [`<opt>`](#opt) elements with global configurations options for [3D View Mode](readme.md#3d-view-mode).

3D View Mode options:

| key | description | type | default |
| :--- | :--- | :--- | :--- |
| **screenshotPath** | Directory for saving map-mode screenshots. | _string_ | screenshots |
| **exposure** | Base camera exposure. | _float_ | 0.85 |
| **contrast** | Base camera contrast. | _float_ | 0.8 |
| **circle** | Map mode circle size multiplier. | _float_ | 12 |
| **startLatitudeMin** | When entering a world, the starting latitude is randomly selected between **startLatitudeMin** and **startLatitudeMax**. Can be between -90 and 90. | _int_ | 20 |
| **startLatitudeMax** | When entering a world, the starting latitude is randomly selected between **startLatitudeMin** and **startLatitudeMax**. Can be between -90 and 90. | _int_ | 70 |

#### Context

[`<stargazer>`](#stargazer)

#### Attributes

None.

#### Contents

[`<opt>`](#opt)


## &lt;chartOptions&gt;

The `<chartOptions>` element contains a set of [`<opt>`](#opt) elements with global configurations options for the [Chart Mode](readme.md#chart-mode).

Chart Mode options:

| key | description | type | default |
| :--- | :--- | :--- | :--- |
| **htmlName** | File name for the brightest stars report (**-stars** option). | _string_ | stars.html |
| **svgCylinderMap** | File name for the cyllindrical projection map. | _string_ | map_cylinder.svg |
| **svgNHemiMap** | File name for the northern hemisphere projection map. | _string_ | map_nhemi.svg |
| **svgSHemiMap** | File name for the southern hemisphere projection map. | _string_ | map_shemi.svg |
| **devPrefix** | File name prefix for development mode maps. | _string_ | dev_ |
| **circle** | Star circle scaling factor. | _float_ | 5.1 |
| **sizeRA** | Size of 1h right ascention span on cylindrical projection in pixels. | _float_ | 345 |
| **sizeDec** | Size of 1&deg; declination span on both cylindrical and hemisphere projections in pixels. | _float_ | 22.75 |
| **margin** | Map margni in pixels | _float_ | 40 |
| **starLabelGap** | Gap between a star circle and a star label in pixels. | _float_ | 3.0 |

#### Context

[`<stargazer>`](#stargazer)

#### Attributes

None.

#### Contents

[`<opt>`](#opt)


## &lt;worlds&gt;

The `<worlds>` element contains the list of pre-defined worlds to appear in [3D View Mode](readme.md#3d-view-mode).
The worlds are defined by generator seeds and options.

#### Context

[`<stargazer>`](#stargazer)

#### Attributes

None.

#### Contents

[`<world>`](worldinfoxml.md#world) as in [world info XML](worldinfoxml.md)

## &lt;opt&gt;

The `<opt>` element is used in various contexts and specifies an option as a key-value pair.

#### Context

[`<options>`](#options), [`<chartOptions>`](#chartoptions), [`<gen>`](worldinfoxml.md#gen) in [world info XML](worldinfoxml.md)

#### Attributes

| attribute | description | type |
| :--- | :--- | :--- |
| **key** | Option name. Required. | _string_ |
| **value** | Option value. Requried. | depends on the option |

#### Context

No child elements.
