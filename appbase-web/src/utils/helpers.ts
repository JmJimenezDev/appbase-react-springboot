export const getCookie = (name: string) => {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop()?.split(";").shift();
  return null;
};

export const formatDateTime = (dateString?: string, options?: Intl.DateTimeFormatOptions) => {
  if (!dateString) return "-";

  const locale = (localStorage.getItem("selectedLanguage") || "es_ES").replace("_", "-");

  return new Intl.DateTimeFormat(locale, {
    dateStyle: "medium",
    timeStyle: "short",
    ...options,
  }).format(new Date(dateString));
};
