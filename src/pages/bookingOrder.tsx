import Grid from "@mui/material/Grid"
import Paper from "@mui/material/Paper"
import { Autocomplete, Button, TextField } from "@mui/material"

import { AppTextField, DataTable, DataTablePagination } from "@/components"

import { AppLayout } from "@/components/App/Layout"
import { bookingStore, commonStore } from "@/store/reducers"
import { useForm } from "react-hook-form"
import FormControllerAutocomplete from "@/components/FormController/Autocomplete"
import { AppAutocomplete } from "@/components/App/Autocomplete"
import _ from "lodash"
import { useDispatch, useSelector } from "react-redux"
import { AppFooter } from "@/components/App/Footer"
import { useState } from "react"
import { defaultValueFilterBooking } from "@/utils/defaultValues"

import { produce } from "immer"
import AppDateField from "@/components/App/DateField"
import { format as formatDate } from "date-fns"

export default function Booking() {
  const dispatch = useDispatch()

  const listBookingOrder = useSelector(bookingStore.selectBookingList)
  const initDataFilter = useSelector(bookingStore.selectInitDataFilter)

  const [dataFilter, setDataFilter] = useState(defaultValueFilterBooking)

  const handleChangeDataFilter = (option, field) => {
    setDataFilter((prev) =>
      produce(prev, (draft) => {
        if (field === "orderNo") {
          draft.orderNo = option
        } else if (field === "fromDate") {
          draft.fromDate = new Date(option)
        } else if (field === "toDate") {
          draft.toDate = new Date(option)
        } else {
          draft[field] = option.map(({ value }) => value)
        }
      })
    )
  }

  const bookingOrderForm = useForm({})

  const handleFilterOrderBooking = () => {
    const transformData = produce(dataFilter, (draft) => {
      draft.fromDate =
        draft.fromDate === "" ? "" : formatDate(draft.fromDate, "yyyy-MM-dd")
      draft.toDate =
        draft.toDate === "" ? "" : formatDate(draft.toDate, "yyyy-MM-dd")
    })

    dispatch(bookingStore.actions.setDefaultValueFilterBooking(transformData))
    handleChangePage(1)
  }

  const handleChangePage = (pageNo: number) => {
    dispatch(commonStore.actions.setTableState({ pageNo }))
    dispatch(bookingStore.sagaGetList())
  }

  const handleChangePerPage = (perPage: number) => {
    dispatch(commonStore.actions.setTableState({ perPage }))
    handleChangePage(1)
  }

  const tableState = useSelector(commonStore.selectTableState)

  const columns = [
    {
      field: "orderNo",
      flex: 0.8,
      headerName: "Order #",
    },
    {
      field: "region",
      flex: 0.8,
      headerName: "Region",
    },
    {
      field: "ctryCode",
      flex: 0.8,
      headerName: "Country",
    },
    {
      field: "dealerName",
      flex: 1.2,
      headerName: "Deale Name",
      renderCell(params) {
        return <span>{params.row.billTo?.dealerDivison}</span>
      },
    },
    {
      field: "Plant",
      flex: 0.8,
      headerName: "Plant",
      renderCell(params) {
        return <span>{params.row.apacSerial?.plant}</span>
      },
    },
    {
      field: "truckClass",
      flex: 0.8,
      headerName: "Class",
      renderCell(params) {
        return <span>{params.row.apacSerial?.metaSeries?.clazz}</span>
      },
    },
    {
      field: "series",
      flex: 0.8,
      headerName: "Series",
      renderCell(params) {
        return <span>{params.row.apacSerial?.metaSeries?.series}</span>
      },
    },
    {
      field: "model",
      flex: 0.8,
      headerName: "Models",
      renderCell(params) {
        return <span>{params.row.apacSerial?.model}</span>
      },
    },
    {
      field: "qty",
      flex: 0.8,
      headerName: "Qty",
    },
    {
      field: "Total Cost",
      flex: 0.8,
      headerName: "Total Cost",
    },
    {
      field: "DN",
      flex: 0.8,
      headerName: "DN",
    },
    {
      field: "DN After Surcharge",
      flex: 0.8,
      headerName: "DN After Surcharge",
    },
    {
      field: "Margin $ After Surcharge",
      flex: 1,
      headerName: "Margin $ After Surcharge",
    },
    {
      field: "Margin % After Surcharge",
      flex: 1,
      headerName: "Margin % After Surcharge",
    },
  ]

  return (
    <>
      <AppLayout entity="booking">
        <Grid container spacing={1}>
          <Grid item xs={4}>
            <Grid item xs={12}>
              <AppTextField
                onChange={(e) =>
                  handleChangeDataFilter(e.target.value, "orderNo")
                }
                name="orderNo"
                label="Order #"
                placeholder="Search order by ID"
              />
            </Grid>
          </Grid>
          <Grid item xs={2}>
            <AppAutocomplete
              options={initDataFilter.regions}
              label="Region"
              sx={{ height: 25, zIndex: 10 }}
              // onChange={handleChangeMatStandards}
              // value={selectedMatStandards}
              limitTags={1}
              disableListWrap
              primaryKeyOption="value"
              multiple
              disableCloseOnSelect
              renderOption={(prop, option) => `${option.value}`}
              getOptionLabel={(option) => `${option.value}`}
            />
          </Grid>
          <Grid item xs={2} sx={{ zIndex: 10, height: 25 }}>
            <AppAutocomplete
              options={initDataFilter.plants}
              label="Plant"
              sx={{ height: 25, zIndex: 10 }}
              onChange={(e, option) => handleChangeDataFilter(option, "plants")}
              // value={selectedMatStandards}
              limitTags={1}
              disableListWrap
              primaryKeyOption="value"
              multiple
              disableCloseOnSelect
              renderOption={(prop, option) => `${option.value}`}
              getOptionLabel={(option) => `${option.value}`}
            />
          </Grid>
          <Grid item xs={2}>
            <AppAutocomplete
              options={initDataFilter.metaSeries}
              label="MetaSeries"
              sx={{ height: 25, zIndex: 10 }}
              onChange={(e, option) =>
                handleChangeDataFilter(option, "metaSeries")
              }
              // value={selectedMatStandards}
              limitTags={1}
              disableListWrap
              primaryKeyOption="value"
              multiple
              disableCloseOnSelect
              renderOption={(prop, option) => `${option.value}`}
              getOptionLabel={(option) => `${option.value}`}
            />
          </Grid>
          <Grid item xs={2} sx={{ zIndex: 10, height: 25 }}>
            <AppAutocomplete
              options={initDataFilter.dealers}
              label="Dealer"
              sx={{ height: 25, zIndex: 10 }}
              onChange={(e, option) =>
                handleChangeDataFilter(option, "dealers")
              }
              // value={selectedMatStandards}
              limitTags={1}
              disableListWrap
              primaryKeyOption="value"
              multiple
              disableCloseOnSelect
              renderOption={(prop, option) => `${option.value}`}
              getOptionLabel={(option) => `${option.value}`}
            />
          </Grid>

          <Grid item xs={2}>
            <AppAutocomplete
              options={initDataFilter.classes}
              label="Class"
              sx={{ height: 25, zIndex: 10 }}
              onChange={(e, option) =>
                handleChangeDataFilter(option, "classes")
              }
              // value={selectedMatStandards}
              limitTags={1}
              disableListWrap
              primaryKeyOption="value"
              multiple
              disableCloseOnSelect
              renderOption={(prop, option) => `${option.value}`}
              getOptionLabel={(option) => `${option.value}`}
            />
          </Grid>
          <Grid item xs={2}>
            <AppAutocomplete
              options={initDataFilter.models}
              label="Model"
              sx={{ height: 25, zIndex: 10 }}
              onChange={(e, option) => handleChangeDataFilter(option, "models")}
              // value={selectedMatStandards}
              limitTags={1}
              disableListWrap
              primaryKeyOption="value"
              multiple
              disableCloseOnSelect
              renderOption={(prop, option) => `${option.value}`}
              getOptionLabel={(option) => `${option.value}`}
            />
          </Grid>
          <Grid item xs={2} sx={{ zIndex: 10, height: 25 }}>
            <AppAutocomplete
              options={initDataFilter.segments}
              label="Segment"
              sx={{ height: 25, zIndex: 10 }}
              onChange={(e, option) =>
                handleChangeDataFilter(option, "segments")
              }
              // value={selectedMatStandards}
              limitTags={1}
              disableListWrap
              primaryKeyOption="value"
              multiple
              disableCloseOnSelect
              renderOption={(prop, option) => `${option.value}`}
              getOptionLabel={(option) => `${option.value}`}
            />
          </Grid>
          <Grid item xs={2}>
            <FormControllerAutocomplete
              control={bookingOrderForm.control}
              name="aopMargin"
              label="AOP Margin %"
              options={[]}
            />
          </Grid>
          <Grid item xs={4}>
            <Grid item xs={6} sx={{ paddingRight: 0.5 }}>
              <FormControllerAutocomplete
                control={bookingOrderForm.control}
                name="margin"
                label="Margin %"
                options={[]}
              />
            </Grid>
          </Grid>
          <Grid item xs={2}>
            <AppDateField
              label="From Date"
              name="from_date"
              onChange={(e, value) => handleChangeDataFilter(value, "fromDate")}
              value={
                dataFilter?.fromDate === ""
                  ? null
                  : formatDate(dataFilter?.fromDate, "yyyy-MM-dd")
              }
            />
          </Grid>
          <Grid item xs={2}>
            <AppDateField
              label="To Date"
              name="toDate"
              onChange={(e, value) => handleChangeDataFilter(value, "toDate")}
              value={
                dataFilter?.toDate === ""
                  ? null
                  : formatDate(dataFilter?.toDate, "yyyy-MM-dd")
              }
            />
          </Grid>
          <Grid item xs={2}>
            <Button
              variant="contained"
              onClick={handleFilterOrderBooking}
              sx={{ width: "100%", height: 24 }}
            >
              Filter
            </Button>
          </Grid>
        </Grid>

        <Paper elevation={1} sx={{ marginTop: 2 }}>
          <Grid container>
            <DataTable
              hideFooter
              disableColumnMenu
              tableHeight={740}
              rowHeight={45}
              rows={listBookingOrder}
              rowBuffer={35}
              rowThreshold={25}
              columns={columns}
              getRowId={(params) => params.orderNo}
            />
          </Grid>
          <DataTablePagination
            page={tableState.pageNo}
            perPage={tableState.perPage}
            totalItems={tableState.totalItems}
            onChangePage={handleChangePage}
            onChangePerPage={handleChangePerPage}
          />
          <AppFooter />
        </Paper>
      </AppLayout>
    </>
  )
}
