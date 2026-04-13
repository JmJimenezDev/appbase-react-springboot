import clsx from "clsx";
import { FiChevronLeft, FiChevronRight, FiChevronsLeft, FiChevronsRight } from "react-icons/fi";

interface PaginatorProps {
    currentPage: number;
    totalPages: number;
    totalElements: number;
    pageSize: number;
    sizes?: number[];
    onPageChange: (page: number) => void;
    onPageSizeChange: (size: number) => void;
    className?: string;
}

export const Paginator = ({
    currentPage,
    totalPages,
    totalElements,
    pageSize,
    sizes = [5, 10, 20, 50],
    onPageChange,
    onPageSizeChange,
    className = "",
}: PaginatorProps) => {
    if (totalPages <= 0) return null;

    const handlePrev = () => {
        if (currentPage > 0) onPageChange(currentPage - 1);
    };

    const handleNext = () => {
        if (currentPage < totalPages - 1) onPageChange(currentPage + 1);
    };

    const handleFirst = () => onPageChange(0);
    const handleLast = () => onPageChange(totalPages - 1);

    const renderPageNumbers = () => {
        const pages: (number | string)[] = [];

        for (let i = 0; i < totalPages; i++) {
            if (
                i === 0 ||
                i === totalPages - 1 ||
                (i >= currentPage - 1 && i <= currentPage + 1)
            ) {
                pages.push(i);
            } else if (
                (i === 1 && currentPage > 3) ||
                (i === totalPages - 2 && currentPage < totalPages - 4)
            ) {
                pages.push("...");
            }
        }

        return pages.filter(
            (page, idx, arr) =>
                !(page === "..." && arr[idx - 1] === "...")
        );
    };

    return <div className={clsx("mt-4 flex flex-col-reverse gap-4 md:flex-row items-center justify-between", className)}>
        <div className="flex items-center gap-5 text-sm text-neutral-500">
            <div className="flex items-center gap-2">
                <span>Filas por página:</span>
                <select
                    value={pageSize}
                    onChange={(e) => {
                        onPageSizeChange(Number(e.target.value));
                        onPageChange(0);
                    }}
                    className="border rounded px-2 py-1"
                >
                    {sizes.map((size) => (
                        <option key={size} value={size}>
                            {size}
                        </option>
                    ))}
                </select>
            </div>

            <span>
                Total: <strong>{totalElements}</strong>
            </span>
        </div>

        <div className="flex items-center space-x-1">
            <button
                onClick={handleFirst}
                disabled={currentPage === 0}
                className="px-2 py-1 border rounded disabled:opacity-50"
            >
                <FiChevronsLeft />
            </button>

            <button
                onClick={handlePrev}
                disabled={currentPage === 0}
                className="px-3 py-1 border rounded disabled:opacity-50"
            >
                <FiChevronLeft />
            </button>

            {renderPageNumbers().map((page, idx) =>
                page === "..." ?
                    <span key={idx} className="px-2">
                        ...
                    </span>
                    :
                    <button
                        key={idx}
                        onClick={() => onPageChange(Number(page))}
                        className={clsx("px-3 py-1 border rounded",
                            page === currentPage && "bg-neutral-900 dark:bg-neutral-100 text-white dark:text-black"
                        )}
                    >
                        {Number(page) + 1}
                    </button>
            )}

            <button
                onClick={handleNext}
                disabled={currentPage >= totalPages - 1}
                className="px-3 py-1 border rounded disabled:opacity-50"
            >
                <FiChevronRight />
            </button>

            <button
                onClick={handleLast}
                disabled={currentPage >= totalPages - 1}
                className="px-2 py-1 border rounded disabled:opacity-50"
            >
                <FiChevronsRight />
            </button>
        </div>
    </div>
};