/*******************************************************
Copyright (C) - DREVER International

This file is part of Malis 3 project.

Malis 3 source code can not be copied and/or distributed without the express permission of DREVER International
*******************************************************/

import { Tooltip, Typography, TooltipProps } from '@mui/material'

import useStyles from './styles'

function ErrorMessage({ title, children, ...props }: TooltipProps) {
  const classes = useStyles()
  return (
    <Tooltip {...props} title={<Typography variant="body2">{title}</Typography>} classes={{ ...classes }}>
      {children}
    </Tooltip>
  )
}

export default ErrorMessage
