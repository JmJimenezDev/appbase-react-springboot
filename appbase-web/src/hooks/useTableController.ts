import { useEffect, useState } from "react";

interface TableControllerParams<TFilters> {
  queryFn: (params: any) => any;
  defaultSort?: string;
  defaultSize?: number;
  defaultFilters?: TFilters;
}

export const useTableController = <TFilters extends Record<string, unknown> = Record<string, never>>({
  queryFn,
  defaultSort = "id,desc",
  defaultSize = 10,
  defaultFilters = {} as TFilters,
}: TableControllerParams<TFilters>) => {
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(defaultSize);

  const [search, setSearch] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");

  const [sort, setSort] = useState(defaultSort);

  const [filters, setFilters] = useState<TFilters>(defaultFilters ?? ({} as TFilters));

  useEffect(() => {
    const timeout = setTimeout(() => {
      setDebouncedSearch(search);
      setPage(0);
    }, 400);

    return () => clearTimeout(timeout);
  }, [search]);

  const query = queryFn({
    page,
    size,
    search: debouncedSearch,
    sort,
    ...filters,
  });

  const updateFilters = (newFilters: Partial<TFilters>) => {
    setPage(0);
    setFilters((prev) => ({ ...prev, ...newFilters }));
  };

  const resetFilters = () => {
    setPage(0);
    setFilters(defaultFilters);
  };

  const updateSearch = (value: string) => {
    setSearch(value);
  };

  const updateSort = (field: string) => {
    setSort((prev) => {
      const [currentField, direction] = prev.split(",");
      if (currentField === field) {
        return `${field},${direction === "asc" ? "desc" : "asc"}`;
      }
      return `${field},asc`;
    });
  };

  const updatePage = (newPage: number) => {
    setPage(newPage);
  };

  const updateSize = (newSize: number) => {
    setSize(newSize);
    setPage(0);
  };

  return {
    ...query,

    page,
    size,
    search,
    sort,
    filters,

    setPage: updatePage,
    setSize: updateSize,
    setSearch: updateSearch,
    setSort: updateSort,
    setFilters: updateFilters,
    resetFilters,
  };
};
