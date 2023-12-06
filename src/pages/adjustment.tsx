import { useCallback, useState } from 'react';

import { formatNumbericColumn } from '@/utils/columnProperties';
import { formatNumber, formatNumberPercentage, formatDate } from '@/utils/formatCell';
import { useDispatch, useSelector } from 'react-redux';
import { adjustmentStore, commonStore } from '@/store/reducers';

import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import { Button } from '@mui/material';

import {
   AppAutocomplete,
   AppDateField,
   AppLayout,
   AppTextField,
   DataTablePagination,
} from '@/components';

import _ from 'lodash';
import { produce } from 'immer';

import { defaultValueFilterOrder } from '@/utils/defaultValues';
import { DataGridPro, GridToolbar } from '@mui/x-data-grid-pro';
import axios from 'axios';
import { parseCookies } from 'nookies';

export async function getServerSideProps(context) {
   try {
      let cookies = parseCookies(context);
      let token = cookies['token'];
      await axios.post(`${process.env.NEXT_PUBLIC_BACKEND_URL}oauth/checkToken`, null, {
         headers: {
            Authorization: 'Bearer ' + token,
         },
      });

      return {
         props: {},
      };
   } catch (error) {
      console.error('token error', error);

      return {
         redirect: {
            destination: '/login',
            permanent: false,
         },
      };
   }
}

export default function Adjustment() {
   const dispatch = useDispatch();
   const listAdjustment = useSelector(adjustmentStore.selectAdjustmentList);
   const initDataFilter = useSelector(adjustmentStore.selectInitDataFilter);

   const [dataFilter, setDataFilter] = useState(defaultValueFilterOrder);

   console.log(dataFilter);

   const handleChangeDataFilter = (option, field) => {
      setDataFilter((prev) =>
         produce(prev, (draft) => {
            if (_.includes(['orderNo', 'fromDate', 'toDate', 'marginPercentage'], field)) {
               draft[field] = option;
            } else {
               draft[field] = option.map(({ value }) => value);
            }
         })
      );
   };

   const handleFilterAdjustment = () => {
      dispatch(adjustmentStore.actions.setDefaultValueFilterAdjustment(dataFilter));
      handleChangePage(1);
   };

   const handleChangePage = (pageNo: number) => {
      dispatch(commonStore.actions.setTableState({ pageNo }));
      dispatch(adjustmentStore.sagaGetList());
   };

   const handleChangePerPage = (perPage: number) => {
      dispatch(commonStore.actions.setTableState({ perPage }));
      handleChangePage(1);
   };

   const tableState = useSelector(commonStore.selectTableState);

   const columns = [
      {
         field: 'region',
         flex: 0.5,
         headerName: 'Region',
         renderCell(params) {
            return <span>{params.row.region}</span>;
         },
      },

      {
         field: 'Plant',
         flex: 0.6,
         headerName: 'Plant',
         renderCell(params) {
            return <span>{params.row.plant}</span>;
         },
      },
      {
         field: 'truckClass',
         flex: 0.6,
         headerName: 'Class',
         renderCell(params) {
            return <span>{params.row.clazz}</span>;
         },
      },
      {
         field: 'series',
         flex: 0.4,
         headerName: 'Series',
         renderCell(params) {
            return <span>{params.row.metaSeries}</span>;
         },
      },
      {
         field: 'model',
         flex: 0.6,
         headerName: 'Models',
         renderCell(params) {
            return <span>{params.row.model}</span>;
         },
      },
      {
         field: 'noOfOrder',
         flex: 0.3,
         headerName: 'Qty',
         ...formatNumbericColumn,
      },

      {
         field: 'manualAdjCost',
         flex: 0.8,
         headerName: 'Adjusted Cost',
         ...formatNumbericColumn,
         renderCell(params) {
            return <span>{formatNumber(params?.row.manualAdjCost)}</span>;
         },
      },
      {
         field: 'manualAdjFreight',
         flex: 0.8,
         headerName: 'Adjusted Freight',
         ...formatNumbericColumn,
         renderCell(params) {
            return <span>{formatNumber(params?.row.manualAdjFreight)}</span>;
         },
      },
      {
         field: 'manualAdjFX',
         flex: 0.7,
         headerName: 'Adjusted FX',
         ...formatNumbericColumn,
         renderCell(params) {
            return <span>{formatNumber(params?.row.manualAdjFX)}</span>;
         },
      },

      {
         field: 'totalManualAdjCost',
         flex: 0.6,
         headerName: 'Total Manual Adj Cost',
         ...formatNumbericColumn,
         renderCell(params) {
            return <span>{formatNumber(params?.row.totalManualAdjCost)}</span>;
         },
      },

      {
         field: 'originalDN',
         flex: 0.7,
         headerName: 'Original DN',
         ...formatNumbericColumn,
         renderCell(params) {
            return <span>{formatNumber(params?.row.originalDN)}</span>;
         },
      },
      {
         field: 'originalMargin',
         flex: 0.7,
         headerName: 'Original Margin $',
         ...formatNumbericColumn,
         renderCell(params) {
            return <span>{formatNumber(params?.row.originalMargin)}</span>;
         },
      },
      {
         field: 'originalMarginPercentage',
         flex: 0.7,
         headerName: 'Original Margin %',
         ...formatNumbericColumn,
         renderCell(params) {
            return (
               <span>{formatNumberPercentage(params?.row.originalMarginPercentage * 100)}</span>
            );
         },
      },

      {
         field: 'newDN',
         flex: 0.6,
         headerName: 'Adjusted Dealer Net',
         ...formatNumbericColumn,
         renderCell(params) {
            return <span>{formatNumber(params?.row.newDN)}</span>;
         },
      },
      {
         field: 'newMargin',
         flex: 0.6,
         headerName: 'New margin $ (USD) - After manual Adj',
         ...formatNumbericColumn,
         renderCell(params) {
            return <span>{formatNumber(params?.row.newMargin)}</span>;
         },
      },
      {
         field: 'newMarginPercentage',
         flex: 0.6,
         headerName: 'New margin % - After manual Adj',
         ...formatNumbericColumn,
         renderCell(params) {
            return <span>{formatNumberPercentage(params?.row.newMarginPercentage * 100)}</span>;
         },
      },
      {
         field: 'additionalVolume',
         flex: 0.6,
         headerName: 'Additional Volume at BEP For Discount',
         ...formatNumbericColumn,
         renderCell(params) {
            return <span>{formatNumber(params?.row.additionalVolume)}</span>;
         },
      },
   ];

   return (
      <>
         <AppLayout entity="adjustment">
            <Grid container spacing={1}>
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
                     options={initDataFilter.marginPercentage}
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
               <Grid item xs={2}>
                  <AppAutocomplete
                     options={initDataFilter.marginPercentage}
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
               <Grid item xs={6}>
                  <Grid item xs={4}>
                     <Button
                        variant="contained"
                        onClick={handleFilterAdjustment}
                        sx={{ width: '100%', height: 24 }}
                     >
                        Filter
                     </Button>
                  </Grid>
               </Grid>
               <Grid item xs={2}>
                  <Grid item xs={12}>
                     <AppTextField
                        // onChange={(e) => handleChangeDataFilter(e.target.value, 'orderNo')}
                        name="orderNo"
                        label="Cost Adj %"
                        placeholder="Search order by ID"
                     />
                  </Grid>
               </Grid>
               <Grid item xs={2}>
                  <Grid item xs={12}>
                     <AppTextField
                        onChange={(e) => handleChangeDataFilter(e.target.value, 'orderNo')}
                        name="orderNo"
                        label="Freight Adj ('000 USD)"
                        placeholder="Search order by ID"
                     />
                  </Grid>
               </Grid>{' '}
               <Grid item xs={2}>
                  <Grid item xs={12}>
                     <AppTextField
                        onChange={(e) => handleChangeDataFilter(e.target.value, 'orderNo')}
                        name="orderNo"
                        label="FX Adj ('000 USD)"
                        placeholder="Search order by ID"
                     />
                  </Grid>
               </Grid>{' '}
               <Grid item xs={2}>
                  <Grid item xs={12}>
                     <AppTextField
                        onChange={(e) => handleChangeDataFilter(e.target.value, 'orderNo')}
                        name="orderNo"
                        label="DN Adj %"
                        placeholder="Search order by ID"
                     />
                  </Grid>
               </Grid>
               <Grid item xs={1}>
                  <Button
                     variant="contained"
                     // onClick={handleImport}
                     sx={{ width: '100%', height: 24 }}
                  >
                     Calculate
                  </Button>
               </Grid>
            </Grid>

            <Paper elevation={1} sx={{ marginTop: 2 }}>
               <Grid container sx={{ height: 'calc(100vh - 229px)' }}>
                  <DataGridPro
                     hideFooter
                     disableColumnMenu
                     //tableHeight={740}
                     rowHeight={30}
                     slots={{
                        toolbar: GridToolbar,
                     }}
                     rows={listAdjustment}
                     rowBuffer={35}
                     rowThreshold={25}
                     columns={columns}
                     getRowId={(params) => params.id}
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
