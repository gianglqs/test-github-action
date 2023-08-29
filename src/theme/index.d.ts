/*******************************************************
Copyright (C) - DREVER International

This file is part of Malis 3 project.

Malis 3 source code can not be copied and/or distributed without the express permission of DREVER International
*******************************************************/

import { Theme } from '@mui/material/styles'

declare module '@mui/material/styles' {
  interface Palette {
    standardDrawing: {
      revision_changed: Palette['primary']
      out_of_sync: Palette['primary']
      synchronized: Palette['primary']
    }
  }
  interface PaletteOptions {
    standardDrawing?: {
      revision_changed: PaletteOptions['primary']
      out_of_sync: PaletteOptions['primary']
      synchronized: PaletteOptions['primary']
    }
  }
}

declare module '@mui/styles/defaultTheme' {
  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  interface DefaultTheme extends Theme {}
}
