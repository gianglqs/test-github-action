
export type DataTablePaginationProps = {
  // countSelectedItems: number
  totalItems: number
  page: number
  perPage: number
  onChangePage(page: number): void
  onChangePerPage(perPage: number): void
  perPageList?: number[]
}
