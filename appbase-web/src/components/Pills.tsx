import clsx from "clsx";
import { Link } from "react-router-dom";

type PillButtonProps = {
    onClick: () => void;
    type?: "red" | "green" | "yellow";
    children: React.ReactNode;
};

export const PillButton = ({ onClick, type = "red", children }: PillButtonProps) => {
    const buttonClasses = clsx(
        "flex items-center w-max cursor-pointer px-3 py-1 rounded-2xl text-xs",
        {
            "bg-red-200 text-red-700 dark:bg-red-950 dark:text-red-500": type === "red",
            "bg-green-200 text-green-700 dark:bg-green-950 dark:text-green-500": type === "green",
            "bg-yellow-200 text-yellow-700 dark:bg-yellow-950 dark:text-yellow-500": type === "yellow",
        }
    );

    return <button onClick={onClick} className={buttonClasses}>
        {children}
    </button>
};

type PillLinkProps = {
    to: string;
    type?: "red" | "green" | "yellow";
    children: React.ReactNode;
};

export const PillLink = ({ to, type = "red", children }: PillLinkProps) => {
    const pillLinkClasses = clsx(
        "flex items-center w-max cursor-pointer px-3 py-1 rounded-full text-xs underline",
        {
            "bg-red-200 text-red-700 dark:bg-red-950 dark:text-red-500": type === "red",
            "bg-green-200 text-green-700 dark:bg-green-950 dark:text-green-500": type === "green",
            "bg-yellow-200 text-yellow-700 dark:bg-yellow-950 dark:text-yellow-500": type === "yellow",
        }
    );

    return (
        <Link to={to} className={pillLinkClasses}>
            {children}
        </Link>
    );
};

type PillProps = {
    type?: "red" | "green" | "yellow";
    children: React.ReactNode;
};

export const Pill = ({ type = "red", children }: PillProps) => {
    const pillClasses = clsx(
        "flex items-center w-max gap-1 px-3 py-1 rounded-full text-xs",
        {
            "bg-red-200 text-red-700 dark:bg-red-950 dark:text-red-500": type === "red",
            "bg-green-200 text-green-700 dark:bg-green-950 dark:text-green-500": type === "green",
            "bg-yellow-200 text-yellow-700 dark:bg-yellow-950 dark:text-yellow-500": type === "yellow",
        }
    );

    return <span className={pillClasses}>
        {children}
    </span>
};