/*******************************************************
Copyright (C) - DREVER International

This file is part of Malis 3 project.

Malis 3 source code can not be copied and/or distributed without the express permission of DREVER International
*******************************************************/

import type { AppDialogProps } from '@/components/Dialog/AppDialog/type'

export interface DialogCalendarProps extends Omit<AppDialogProps, 'onChange'> {
  date: string
  disabled?: boolean
  onChange: (event, stringDate: string, date: Date) => void
  minDate?: Date | string
  maxDate?: Date | string
}
