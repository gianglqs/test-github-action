/*******************************************************
Copyright (C) - DREVER International

This file is part of Malis 3 project.

Malis 3 source code can not be copied and/or distributed without the express permission of DREVER International
*******************************************************/

import type { StandardTextFieldProps } from '@mui/material'

export interface AppTextFieldProps extends StandardTextFieldProps {
  generateCode?: boolean
  tooltip?: string
  loading?: boolean
  onGenerateCode?: (event) => void
  isFocus?: boolean
}
