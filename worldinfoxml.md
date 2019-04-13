# World Info XML Format


## Terminology

### Right Ascension (RA)

See [Right Ascension](https://en.wikipedia.org/wiki/Right_ascension) on Wikipedia.

Values range from 0h to 24h, one hour is subdivided into 60 minutes. In XML, RA is formatted as `hh:mm`, for example, `9:20` corresponds to 9h20m.

RA deltas can be specified as `+hh:mm` (positive) or `-hh:mm` (negative).

### Declination (Dec)

See [Declination](https://en.wikipedia.org/wiki/Declination) on Wikipedia.

Values are in degrees ranging from -90&deg; to +90&deg;. In XML, Dec is formatted as _float_, for example, `+10.5` corresponds to 10.5&deg;.

### Star ID

Stars are numbered by brightness. The brightest star has the ID of 1.

Generate `stars.html` (**-stars** option) or development mode maps (**-dev** option) to find the IDs of specific stars.


## &lt;world&gt;

World definition.

#### Context

* Root element in world info XML.
* [`<worlds>`](optionsxml.md#worlds) in `options.xml` (see [Config XML Format](optionsxml.md)).

#### Attributes

| attribute | description | type |
| :--- | :--- | :--- |
| **title** | Displayed world name. | _string_ |
| **seed** | Generator seed (64-bit). Requried. | _int_ |
| **info** | Link to world info XML. Only valid in `options.xml`, ignored in world XML. | _string_ |

#### Content

[`<gen>`](#gen), [`<stars>`](#stars), [`<constellations>`](#constellations)


## &lt;gen&gt;

The `<gen>` element contains a set of [`<opt>`](optionsxml.md#opt) elements defining generator options for the world.

Generator options:

| key | description | type | default |
| :--- | :--- | :--- | :--- |
| **numStars** | Number of generated stars. Only a very small fraction of generated stars will be visible to a naked eye. | _int_ | 1000000 |
| **distributionSigma** | Spacial distribution parameter: standard deviation of normal distribution. | _float_ | 0.05 |
| **distributionOffset** | Spacial distribution parameter: fraction of uniformly distributed stars. | _float_ | 0.6 |
| **brightnessLambda** | Star brightness parameter: lambda coefficient for exponential distribution. | _float_ | 8.0 |
| **brightnessOffset** | Star brightness parameter: constant bias. | _float_ | 0.05 |
| **sphere** | Crop to pherical volume. Must be true, added for backwards compatibility. | _boolean_ | true |

#### Context

[`<world>`](#world)

#### Attributes

None.

#### Content

[`<opt>`](optionsxml.md#opt)



## &lt;stars&gt;

List of named stars.

#### Context

[`<world>`](#world)

#### Attributes

None.

#### Content

[`<s>`](#s)


## &lt;s&gt;

Named star.

#### Context

[`<stars>`](#stars)

#### Attributes

| attribute | description | type |
| :--- | :--- | :--- |
| **id** | Star ID. Required. | _int_ |
| **name** | Star name. Requried. | _string_ |
| **altname** | Optional alternative name. | _string_ |

#### Content

No child elements.


## &lt;constellations&gt;

List of constellations.

#### Context

[`<world>`](#world)

#### Attributes

None.

#### Content

[`<con>`](#con)


## &lt;con&gt;

Constellation description.

#### Context

[`<constellations>`](#constellations)

#### Attributes

| attribute | description | type |
| :--- | :--- | :--- |
| **name** | Constellation name. Requried. | _string_ |
| **minor** | Is this a "minor" constellation? Optional, default is `no`. | _bool_ |
| **an** | Constellation name location on map ([RA](#right-ascension-ra)). Optional. By default the name is located in the middle given the borders are correctly defined. | _float_ |
| **dn** | Constellation name location on map ([Dec](#declination-dec)). Optional. By default the name is located in the middle given the borders are correctly defined. | _float_ |
| **labelstars** | How many brightest stars to label with greek letters. Default is only one (alpha). | _int_ |

#### Content

[`<border>`](#borders), [`<lines>`](#lines)


## &lt;border&gt;

Constellation border definition.

#### Context

[`<con>`](#con)

#### Attributes

| attribute | description | type |
| :--- | :--- | :--- |
| **as** | Starting point location ([RA](#right-ascension-ra)) | _float_ |
| **ds** | Starting point location ([Dec](#declination-dec)) | _float_ |

#### Content

Text describing the border as the series of alternating horizontal and vertical segments, starting from horizontal.

_Horizontal segments_ go along constant Dec and appear as horizontal lines on a cylindrical projection and arcs on hemisphere projections, they are defined as RA deltas.

_Vertical segments_ go along constant RA and appear as vertical lines on a cylindrical projection and radial lines on hemisphere projections, they are defined as Dec deltas.

The borders are automatically closed by connecting the last defined point with the starting point.

Horizontal segment cannot be longer than 12h. If needed, split it in two shorter segments with a zero-length vertical segment in between.

#### Example

```
<border as="12:46" ds="-1">-0:37 -9 +0:23 +1</border>
```

defines the following border segments:
* (12h46m, -1&deg;) to (12h09m, -1&deg;), horizontal
* (12h09m, -1&deg;) to (12h09m, -10&deg;), vertical
* (12h09m, -10&deg;) to (12h32m, -10&deg;), horizontal
* (12h32m, -10&deg;) to (12h32m, -9&deg;), vertical
* _automatic_ (12h32m, -9&deg;) to (12h46m, -9&deg;), horizontal
* _automatic_ (12h46m, -9&deg;) to (12h46m, -1&deg;), vertical


## &lt;lines&gt;

Constellation lines definition.

#### Context

[`<con>`](#con)

#### Attributes

None.

#### Content

Text describing lines using semicolon-separated pairs of [star IDs](#star-id), for example:

```
13 166; 166 74; 74 162; 74 651
```

creates the lines between stars:
* 13 and 166
* 166 and 74
* 74 and 162
* 74 and 651

Sequentially connected stars can be grouped together. In the previous example, stars 13, 166, 74, and 162 are connected sequentially, so the notation can be shortened to:

```
13 166 74 162; 74 651
```

Line thickness depends on the brightness of connected stars.
