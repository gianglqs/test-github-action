/*******************************************************
Copyright (C) - DREVER International

This file is part of Malis 3 project.

Malis 3 source code can not be copied and/or distributed without the express permission of DREVER International
*******************************************************/

import type { OutlinedTextFieldProps, AutocompleteProps } from '@mui/material'

type DropdownInputProps = 'label' | 'required' | 'error' | 'helperText'

export interface AppAutocompleteProps<T>
  extends Omit<AutocompleteProps<T, boolean, boolean, boolean>, 'renderInput'>,
    Pick<OutlinedTextFieldProps, DropdownInputProps> {
  textFieldProps?: Omit<OutlinedTextFieldProps, 'variant' | 'label' | 'required' | 'error' | 'helperText'>
  primaryKeyOption?: string
  variant?: string
  isFocus?: boolean
}
