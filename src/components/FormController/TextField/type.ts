/*******************************************************
Copyright (C) - DREVER International

This file is part of Malis 3 project.

Malis 3 source code can not be copied and/or distributed without the express permission of DREVER International
*******************************************************/

// import type { UseControllerOptions, UseControllerProps } from 'react-hook-form'
import type { AppTextFieldProps } from '@/components/App/TextField/type'

export interface ControlledTextFieldProps
  extends Omit<AppTextFieldProps, 'name' | 'variant'>,
    Omit<any, 'onFocus'> {
  transformValue?(value): any
  limitText?: number
  variant?: 'filled' | 'outlined' | 'standard'
  control: any
}
