# Stargazer

> _Other worlds Astronomy._

### Download

[stargazer-1.0.0.zip](https://github.com/ashurrafiev/Stargazer/releases/download/1.0.0/stargazer-1.0.0.zip) (3.08 MB)

### Usage

[3D view mode](#3d-view-mode):

```
java -jar stargazer.jar
```

[Chart mode](#chart-mode):

```
java -jar stargazer.jar -C chart_mode_options
```

_Note:_ `-C` option must go first to enable chart mode.

### Example world

[Stargazer I](https://ashurrafiev.github.io/Stargazer/worlds/stargazer_i/stars.html) (work in progress).



## 3D View Mode

### Mouse

| button | action |
| :---: | :--- |
| **SCROLL** | Increase/reduce camera exposure. |
| **MMB** | Reset exposure to its default value. |
| **RMB** | Toggle mouse look. |

### Hotkeys

| key | action |
| :---: | :--- |
| **TAB** | Toggle between map and realistic modes. |
| **&grave;** | Save screenshot as map mode to `screenshots` folder (can be changed in options). |
| **[** | Previous world. |
| **]** | Next world. |
| **BKSP** | Reset current world time and location. |
| **ENTER** | Create a random world. |
| **-** | Change latitude by -5&deg;. |
| **=** | Change latitude by +5&deg;. |
| **0** | Set latitude to the closest pole or switch between North and South poles. |
| **9** | Set day time to zero. |
| **ESC** | Exit now. No questions asked. |

### Additional debug keys

| key | action |
| :---: | :--- |
| **F1** | Toggle V-sync. |
| **F2** | Toggle wireframe. Only affects terrain rendering. |
| **F10** | Toggle FPS display and/or debug info. |
| **F11** | Toggle between windowed and borderless fullscreen modes. |
| **F12** | Save screenshot as is in the application root folder. |

Additional options can be defined in `config/options.xml` (see [Config XML Format](optionsxml.md)).

## Chart Mode

### Command line options

All options are case-sensitive.

| option | description |
| :--- | :--- |
| **-C** | _Required, must go first._ Enables chart mode. |
| **-in**&nbsp;filename | _Required._ [World info](worldinfoxml.md) XML file. |
| **-mag**&nbsp;float | Optional. Only include stars brighter than this apparent magnitude. Default is +5.0. |
| **-out**&nbsp;directory | Optional. Path to output files. Defalult is current folder. |
| **-stars**&nbsp;int | Optional. Creates `stars.html` file with the list of N brightest stars. |
| **-nhemi** | Optional. Render Northern hemisphere projection map as `map_nhemi.svg`. |
| **-shemi** | Optional. Render Southern hemisphere projection map as `map_shemi.svg`. |
| **-cylinder** | Optional. Render cylindrical projection map as `map_cylinder.svg`. |
| **-maps** | Optional. Render all map files (same as `-nhemi -shemi -cylinder`). |
| **-dev** | Optional. Render maps in development mode. |
| **-idmag**&nbsp;float | Optional. In development mode, only show IDs for stars brighter than this apparent magnitude. Default is +4.4. |

At least one output option (`-stars`, `-maps`, `-nhemi`, `-shemi`, or `-cylinder`) is required.

Additional options can be defined in `config/options.xml` (see [Config XML Format](optionsxml.md)).
`config/stars_template.html` contains the template for `stars.html`.
`config/map_svg.css` contains the stylesheet for map SVGs.

_Example:_  
This will generate maps for [Stargazer I](worlds/stargazer_i/info.xml):
```
java -jar stargazer.jar -C -in worlds/stargazer_i/info.xml -out worlds/stargazer_i -maps
```

## Advanced Configuration

* [Editing Config XML](optionsxml.md)
* [Creating Your Worlds](worldinfoxml.md)
