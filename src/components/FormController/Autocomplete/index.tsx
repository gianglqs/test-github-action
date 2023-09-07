import { forwardRef } from "react"
import { useController } from "react-hook-form"

import _ from "lodash"
import { makeStyles } from "@mui/styles"

import { AppAutocomplete } from "@/components/App/Autocomplete"

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
    primaryKeyOption,
    onChange: onChangeProps,
    textFieldProps,
    ...autocompleteProps
  } = props

  const {
    field: { onChange, ...inputProps },
    fieldState: { invalid },
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
      helperText={(invalid as any)?.message}
      error={Boolean(invalid)}
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
