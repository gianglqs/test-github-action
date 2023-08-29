/*******************************************************
Copyright (C) - DREVER International

This file is part of Malis 3 project.

Malis 3 source code can not be copied and/or distributed without the express permission of DREVER International
*******************************************************/

// import { useCallback } from 'react'
import {
    DataGridPro,
    DataGridProProps,
    GridRowId,
    GridToolbarColumnsButton,
    GridToolbarContainer,
    GridToolbarDensitySelector,
} from '@mui/x-data-grid-pro'

export interface DataTableProps extends DataGridProProps {
tableHeight?: number | string
hideFooter?: boolean
page?: number
perPage?: number
totalItems?: number
showToolbar?: boolean
entity?: string
onChangePage?(page: number): void
onChangePerPage?(perPage: number): void
}

const DataTable: React.FC<any> = (props) => {
const {
    tableHeight,
    hideFooter,
    entity,
    page,
    // density,
    showToolbar,
    perPage,
    totalItems,
    selectionModel,
    autoHeight,
    onChangePage,
    onChangePerPage,
    ...rest
} = props
return (
    <>
    <div style={{ height: autoHeight ? 'auto' : tableHeight, width: '100%' }} role="table">
        <DataGridPro
        autoHeight={autoHeight}
        selectionModel={selectionModel}
        hideFooter
        // density={handleSetDentity(valueDensity)}
        // getRowHeight={getRowHeight}
        rowBuffer={35}
        rowThreshold={25}
        {...rest}
        />
    </div>

    {/* <Unless condition={hideFooter}>
        <DataTablePagination
        countSelectedItems={(selectionModel as GridRowId[])?.length || 0}
        page={page}
        perPage={perPage}
        totalItems={totalItems}
        onChangePage={onChangePage}
        onChangePerPage={onChangePerPage}
        />
    </Unless> */}
    </>
)
}

DataTable.defaultProps = {
headerHeight: 30,
rowHeight: 30,
hideFooter: false,
density: 'compact',
showToolbar: false
}

export default DataTable
  