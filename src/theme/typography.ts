/*******************************************************
Copyright (C) - DREVER International

This file is part of Malis 3 project.

Malis 3 source code can not be copied and/or distributed without the express permission of DREVER International
*******************************************************/

import type { TypographyOptions } from '@mui/material/styles/createTypography'

// export const FONT_FAMILY = 'sans-serif, "Arial", "Roboto", "Inter", "Helvetica"'
export const FONT_FAMILY = '"Open Sans", sans-serif, "Arial"'
export const FONT_SIZE_BODY_1 = 13
export const FONT_SIZE_BODY_2 = 12

const typography: TypographyOptions = {
  fontFamily: FONT_FAMILY,
  body1: {
    fontSize: FONT_SIZE_BODY_1
  },
  body2: {
    fontSize: FONT_SIZE_BODY_2
  }
}

export default typography
