/*******************************************************
Copyright (C) - DREVER International

This file is part of Malis 3 project.

Malis 3 source code can not be copied and/or distributed without the express permission of DREVER International
*******************************************************/

import { makeStyles } from '@mui/styles'
import { red } from '@mui/material/colors'

const useStyles = makeStyles((theme) => ({
  tooltip: {
    backgroundColor: red[100],
    color: 'rgba(0, 0, 0, 0.87)',
    maxWidth: 220,
    border: `1px solid ${red[200]}`,
    fontWeight: theme.typography?.fontWeightMedium
  },
  arrow: {
    color: red[200]
  }
}))

export default useStyles
