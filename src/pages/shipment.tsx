import { useState } from 'react';

import { useDispatch, useSelector } from 'react-redux';
import { shipmentStore, commonStore } from '@/store/reducers';

import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import { Button } from '@mui/material';

import {
   AppAutocomplete,
   AppDateField,
   AppLayout,
   AppTextField,
   DataTable,
   DataTablePagination,
} from '@/components';

import _ from 'lodash';
import { produce } from 'immer';

import { defaultValueFilterShipment } from '@/utils/defaultValues';

export default function Shipment() {
   const dispatch = useDispatch();

   const listShipment = useSelector(shipmentStore.selectShipmentList);
   console.log('data ' + listShipment);
   const initDataFilter = useSelector(shipmentStore.selectInitDataFilter);

   const [dataFilter, setDataFilter] = useState(defaultValueFilterShipment);
   console.log('data filter ' + initDataFilter);

   const handleChangeDataFilter = (option, field) => {
      setDataFilter((prev) =>
         produce(prev, (draft) => {
            if (
               _.includes(
                  ['orderNo', 'fromDate', 'toDate', 'marginPercentage', 'aopMarginPercentageGroup'],
                  field
               )
            ) {
               draft[field] = option;
            } else {
               draft[field] = option.map(({ value }) => value);
            }
         })
      );
   };

   const handleFilterOrderShipment = () => {
      dispatch(shipmentStore.actions.setDefaultValueFilterShipment(dataFilter));
      handleChangePage(1);
   };

   const handleChangePage = (pageNo: number) => {
      dispatch(commonStore.actions.setTableState({ pageNo }));
      dispatch(shipmentStore.sagaGetList());
   };

   const handleChangePerPage = (perPage: number) => {
      dispatch(commonStore.actions.setTableState({ perPage }));
      handleChangePage(1);
   };

   const formatNumber = (num: number) => {
      return num.toLocaleString(undefined, {
         minimumFractionDigits: 2,
         maximumFractionDigits: 2,
      });
   };
   const tableState = useSelector(commonStore.selectTableState);

   const columns = [
      {
         field: 'orderNo',
         flex: 0.4,
         headerName: 'Order #',
      },
      {
         field: 'date',
         flex: 0.5,
         headerName: 'Create at',
      },
      {
         field: 'region',
         flex: 0.3,
         headerName: 'Region',
         renderCell(params) {
            return <span>{params.row.region?.region}</span>;
         },
      },
      {
         field: 'ctryCode',
         flex: 0.3,
         headerName: 'Country',
      },

      {
         field: 'Plant',
         flex: 0.5,
         headerName: 'Plant',
         renderCell(params) {
            return <span>{params.row.productDimension?.plant}</span>;
         },
      },
      {
         field: 'truckClass',
         flex: 0.7,
         headerName: 'Class',
         renderCell(params) {
            return <span>{params.row.productDimension?.clazz}</span>;
         },
      },
      {
         field: 'dealerName',
         flex: 1.2,
         headerName: 'Dealer Name',
      },
      {
         field: 'series',
         flex: 0.4,
         headerName: 'Series',
         renderCell(params) {
            return <span>{params.row.series}</span>;
         },
      },
      {
         field: 'model',
         flex: 0.6,
         headerName: 'Models',
         renderCell(params) {
            return <span>{params.row.productDimension?.model}</span>;
         },
      },
      {
         field: 'quantity',
         flex: 0.2,
         headerName: 'Qty',
      },

      {
         field: 'dealerNet',
         flex: 0.8,
         headerName: 'DN',
         renderCell(params) {
            return <span>{formatNumber(params?.row.dealerNet)}</span>;
         },
      },
      {
         field: 'dealerNetAfterSurCharge',
         flex: 0.8,
         headerName: 'DN After Surcharge',
         renderCell(params) {
            return <span>{formatNumber(params?.row.dealerNetAfterSurCharge)}</span>;
         },
      },
      {
         field: 'totalCost',
         flex: 0.8,
         headerName: 'Total Cost',
         renderCell(params) {
            return <span>{formatNumber(params?.row.totalCost)}</span>;
         },
      },
      {
         field: 'netRevenue',
         flex: 0.8,
         headerName: 'Net Revenue',
         renderCell(params) {
            return <span>{formatNumber(params?.row.netRevenue)}</span>;
         },
      },
      {
         field: 'marginAfterSurCharge',
         flex: 0.8,
         headerName: 'Margin $ After Surcharge',
         renderCell(params) {
            return <span>{formatNumber(params?.row.marginAfterSurCharge)}</span>;
         },
      },

      {
         field: 'marginPercentageAfterSurCharge',
         flex: 0.6,
         headerName: 'Margin % After Surcharge',
         renderCell(params) {
            return <span>{formatNumber(params?.row.marginPercentageAfterSurCharge * 100)}%</span>;
         },
      },
      {
         field: 'aopmarginPercentage',
         flex: 0.6,
         headerName: 'AOP Margin%',
         renderCell(params) {
            return <span>{formatNumber(params?.row.aopmarginPercentage * 100)}%</span>;
         },
      },
   ];

   return (
      <>
         <AppLayout entity="shipment">
            <Grid container spacing={1}>
               <Grid item xs={4}>
                  <Grid item xs={12}>
                     <AppTextField
                        onChange={(e) => handleChangeDataFilter(e.target.value, 'orderNo')}
                        name="orderNo"
                        label="Order #"
                        placeholder="Search order by ID"
                     />
                  </Grid>
               </Grid>
               <Grid item xs={2} sx={{ zIndex: 10, height: 25 }}>
                  <AppAutocomplete
                     options={initDataFilter.regions}
                     label="Region"
                     onChange={(e, option) => handleChangeDataFilter(option, 'regions')}
                     limitTags={2}
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
                     onChange={(e, option) => handleChangeDataFilter(option, 'plants')}
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
                     onChange={(e, option) => handleChangeDataFilter(option, 'metaSeries')}
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
                     onChange={(e, option) => handleChangeDataFilter(option, 'dealers')}
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
                     onChange={(e, option) => handleChangeDataFilter(option, 'classes')}
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
                     onChange={(e, option) => handleChangeDataFilter(option, 'models')}
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
                     onChange={(e, option) => handleChangeDataFilter(option, 'segments')}
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
                     options={initDataFilter.marginPercentageGroup}
                     label="Margin %"
                     onChange={(e, option) =>
                        handleChangeDataFilter(
                           _.isNil(option) ? '' : option?.value,
                           'marginPercentage'
                        )
                     }
                     disableClearable={false}
                     primaryKeyOption="value"
                     renderOption={(prop, option) => `${option.value}`}
                     getOptionLabel={(option) => `${option.value}`}
                  />
               </Grid>
               <Grid item xs={4} sx={{ paddingRight: 0.5 }}>
                  <Grid item xs={6}>
                     <AppAutocomplete
                        options={initDataFilter.AOPMarginPercentageGroup}
                        label="AOP Margin %"
                        primaryKeyOption="value"
                        onChange={(e, option) =>
                           handleChangeDataFilter(
                              _.isNil(option) ? '' : option?.value,
                              'aopMarginPercentageGroup'
                           )
                        }
                        disableClearable={false}
                        renderOption={(prop, option) => `${option.value}`}
                        getOptionLabel={(option) => `${option.value}`}
                     />
                  </Grid>
               </Grid>
               <Grid item xs={2}>
                  <AppDateField
                     label="From Date"
                     name="from_date"
                     onChange={(e, value) =>
                        handleChangeDataFilter(_.isNil(value) ? '' : value, 'fromDate')
                     }
                     value={dataFilter?.fromDate}
                  />
               </Grid>
               <Grid item xs={2}>
                  <AppDateField
                     label="To Date"
                     name="toDate"
                     onChange={(e, value) =>
                        handleChangeDataFilter(_.isNil(value) ? '' : value, 'toDate')
                     }
                     value={dataFilter?.toDate}
                  />
               </Grid>
               <Grid item xs={2}>
                  <Button
                     variant="contained"
                     onClick={handleFilterOrderShipment}
                     sx={{ width: '100%', height: 24 }}
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
                     rows={listShipment}
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
   );
}
