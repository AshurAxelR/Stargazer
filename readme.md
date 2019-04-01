# Stargazer

> _Other worlds Astronomy._

### Usage

* [3D view mode](#3d-view-mode):

  ```
java -jar stargazer.jar
  ```

* [Chart mode](#chart-mode):

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
| **&#x0060;** | Save screenshot as map mode to `screenshots` folder (can be changed in options). |
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


## Chart Mode

### Command line options

All options are case-sensitive.

| key | action |
| :--- | :--- |
| **-C** | _Required, must go first._ Enables chart mode. |
| **-in** filename | _Required._ World info XML file. |
| **-mag** float | Optional. Only include stars brighter than this apparent magnitude. Default is +5.0. |
| **-out** directory | Optional. Path to output files. Defalult is current folder. |
| **-stars** int | Optional. Creates `stars.html` file with the list of N brightest stars. |
| **-nhemi** | Optional. Render Northern hemisphere projection map as `map_nhemi.svg`. |
| **-shemi** | Optional. Render Southern hemisphere projection map as `map_shemi.svg`. |
| **-cylinder** | Optional. Render cylindrical projection map as `map_cylinder.svg`. |
| **-maps** | Optional. Render all map files (same as `-nhemi -shemi -cylinder`). |
| **-dev** | Optional. Render maps in development mode. |
| **-idmag** float | Optional. In development mode, only show IDs for stars brighter than this apparent magnitude. Default is +4.4. |

At least one output option (`-stars`, `-maps`, `-nhemi`, `-shemi`, or `-cylinder`) is required.

## Advanced Configuration

* Editing Config XML - _TODO_
* Creating Your Worlds - _TODO_
