import React, { useEffect, type ReactNode } from "react";
import { useTranslation } from "react-i18next";
import { IoCloseOutline } from "react-icons/io5";

interface ModalProps {
    isOpen: boolean;
    onClose: () => void;
    children: ReactNode;
    closeOnOutsideClick?: boolean;
    className?: string;
}

interface ModalSubComponentProps {
    children: ReactNode;
}

export const Modal: React.FC<ModalProps> & {
    Header: React.FC<ModalSubComponentProps>;
    Body: React.FC<ModalSubComponentProps>;
    Footer: React.FC<ModalSubComponentProps>;
} = ({ isOpen, onClose, children, closeOnOutsideClick = true, className = "" }) => {
    const { t } = useTranslation();

    useEffect(() => {
        const handleEsc = (event: KeyboardEvent) => {
            if (event.key === "Escape") onClose();
        };
        if (isOpen) document.addEventListener("keydown", handleEsc);
        return () => document.removeEventListener("keydown", handleEsc);
    }, [isOpen, onClose]);

    if (!isOpen) return null;

    const handleOverlayClick = () => {
        if (closeOnOutsideClick) onClose();
    };

    const handleModalClick = (e: React.MouseEvent) => e.stopPropagation();

    return <div onClick={handleOverlayClick} className="fixed inset-0 z-80 flex items-center justify-center bg-black/50">
        <div onClick={handleModalClick} className={`bg-neutral-100 dark:bg-neutral-800 rounded-lg shadow-lg max-w-lg w-full relative ${className}`}>
            <button onClick={onClose} aria-label={t("others.close-modal")} className="absolute top-3 right-3 rounded-full text-gray-500 hover:text-gray-700 transition">
                <IoCloseOutline className="size-6" />
            </button>
            {children}
        </div>
    </div>
};

Modal.Header = ({ children }) => <>
    <div className="text-xl font-bold px-6 py-4">{children}</div>
    <hr className='text-neutral-300 dark:text-neutral-600 mb-4' />
</>;

Modal.Body = ({ children }) => <div className="p-6">{children}</div>;

Modal.Footer = ({ children }) => <>
    <hr className='text-neutral-300 dark:text-neutral-600 mt-4' />
    <div className="flex justify-end gap-2 px-6 py-4">{children}</div>
</>;