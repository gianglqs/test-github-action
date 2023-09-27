import { useState } from "react"

import { useDispatch, useSelector } from "react-redux"
import { bookingStore, commonStore } from "@/store/reducers"

import Grid from "@mui/material/Grid"
import Paper from "@mui/material/Paper"
import { Button } from "@mui/material"

import {
  AppAutocomplete,
  AppDateField,
  AppLayout,
  AppTextField,
  DataTable,
  DataTablePagination,
} from "@/components"

import _ from "lodash"
import { produce } from "immer"

import { defaultValueFilterBooking } from "@/utils/defaultValues"

export default function Booking() {
  const dispatch = useDispatch()
  const listBookingOrder = useSelector(bookingStore.selectBookingList)
  const initDataFilter = useSelector(bookingStore.selectInitDataFilter)

  const [dataFilter, setDataFilter] = useState(defaultValueFilterBooking)

  const handleChangeDataFilter = (option, field) => {
    setDataFilter((prev) =>
      produce(prev, (draft) => {
        if (_.includes(["orderNo", "fromDate", "toDate"], field)) {
          draft[field] = option
        } else {
          draft[field] = option.map(({ value }) => value)
        }
      })
    )
  }

  const handleFilterOrderBooking = () => {
    dispatch(bookingStore.actions.setDefaultValueFilterBooking(dataFilter))
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
      headerName: "Dealer Name",
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

  const arrTmp = [
    { value: "dealer" },
    { value: "delser2" },
    { value: "dealer3" },
    { value: "delser4" },
    { value: "delser5" },
    { value: "delser6" },
    { value: "delser7" },
    { value: "delser8" },
    { value: "delser9" },
    { value: "delser10" },
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
              options={arrTmp}
              label="Region"
              limitTags={2}
              disableListWrap
              primaryKeyOption="value"
              freeSolo={true}
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
            <AppAutocomplete options={[]} label="AOP Margin %" />
          </Grid>
          <Grid item xs={4}>
            <Grid item xs={6} sx={{ paddingRight: 0.5 }}>
              <AppAutocomplete options={[]} label="Margin %" />
            </Grid>
          </Grid>
          <Grid item xs={2}>
            <AppDateField
              label="From Date"
              name="from_date"
              onChange={(e, value) => handleChangeDataFilter(value, "fromDate")}
              value={dataFilter?.fromDate}
            />
          </Grid>
          <Grid item xs={2}>
            <AppDateField
              label="To Date"
              name="toDate"
              onChange={(e, value) => handleChangeDataFilter(value, "toDate")}
              value={dataFilter?.toDate}
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
        </Paper>
      </AppLayout>
    </>
  )
}
