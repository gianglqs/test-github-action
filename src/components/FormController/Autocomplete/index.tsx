import { forwardRef } from "react"
import { useController } from "react-hook-form"

import _ from "lodash"
import { makeStyles } from "@mui/styles"
import { AppAutocomplete } from '@/components/App'

const useStyles = makeStyles(() => ({
  popper: {
    zIndex: 9999999,
  },
}))

const FormControllerAutocomplete = forwardRef<any, any>((props, ref) => {
  const classes = useStyles()
  const {
    control,
    defaultValue,
    name,
    rules,
    disabled,
    primaryKeyOption,
    onChange: onChangeProps,
    textFieldProps,
    multiple,
    ...autocompleteProps
  } = props

  const {
    field: { onChange, ...inputProps },
    fieldState: { invalid, error },
  } = useController({
    name,
    rules,
    defaultValue,
    control,
  })

  const onChangeValue = (event, value) => {
    if (_.isNil(value)) {
      onChange(null)
      return
    }
    onChange(value[primaryKeyOption])

    if (_.isFunction(onChangeProps)) {
      onChangeProps(value)
    }
  }

  return (
    <AppAutocomplete
      {...autocompleteProps}
      {...inputProps}
      ref={ref}
      classes={{ ...classes }}
      helperText={error?.message}
      disabled={disabled}
      error={Boolean(invalid)}
      multiple={multiple}
      onChange={onChangeValue}
      textFieldProps={{ name: name, ...textFieldProps }}
      primaryKeyOption={primaryKeyOption}
    />
  )
})

FormControllerAutocomplete.defaultProps = {
  primaryKeyOption: "id",
}

export default FormControllerAutocomplete
