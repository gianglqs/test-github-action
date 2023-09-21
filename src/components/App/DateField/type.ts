/*******************************************************
Copyright (C) - DREVER International

This file is part of Malis 3 project.

Malis 3 source code can not be copied and/or distributed without the express permission of DREVER International
*******************************************************/

import type { AppTextFieldProps } from '../TextField/type'

export interface AppDateFieldProps extends Omit<AppTextFieldProps, 'onChange'> {
  onChange: (event, date: string) => void
  minDate?: string | Date
  maxDate?: string | Date
  disabled?: boolean
}
