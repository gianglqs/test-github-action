import { DataGridPro, DataGridProProps } from "@mui/x-data-grid-pro"

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
      <div
        style={{ height: autoHeight ? "auto" : tableHeight, width: "100%" }}
        role="table"
      >
        <DataGridPro
          autoHeight={autoHeight}
          selectionModel={selectionModel}
          hideFooter
          rowBuffer={35}
          rowThreshold={25}
          {...rest}
        />
      </div>
    </>
  )
}

DataTable.defaultProps = {
  headerHeight: 30,
  rowHeight: 30,
  hideFooter: false,
  density: "compact",
  showToolbar: false,
}

export default DataTable
