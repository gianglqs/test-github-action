import { Tooltip, Typography, TooltipProps } from "@mui/material"

import useStyles from "./styles"

function ErrorMessage({ title, children, ...props }: TooltipProps) {
  const classes = useStyles()
  return (
    <Tooltip
      {...props}
      title={<Typography variant="body2">{title}</Typography>}
      classes={{ ...classes }}
    >
      {children}
    </Tooltip>
  )
}

export default ErrorMessage
