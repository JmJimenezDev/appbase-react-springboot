# AppBase Web

Frontend web application built with React, TypeScript, and Vite.

## Tech Stack

- **Framework**: React 19 + TypeScript
- **Build Tool**: Vite 7
- **Styling**: Tailwind CSS 4
- **State Management**: Jotai (atoms)
- **Data Fetching**: TanStack Query (React Query)
- **Form Handling**: React Hook Form + Zod
- **Internationalization**: i18next + react-i18next
- **Routing**: React Router DOM 7
- **HTTP Client**: Axios
- **Notifications**: React Toastify
- **Icons**: React Icons

## Project Structure

```
src/
├── api/                  # API calls
│   └── users.api.ts
├── atoms/                # Jotai state atoms
│   ├── authAtom.ts
│   ├── loaderAtom.ts
│   └── preferencesLayoutAtom.ts
├── components/           # Reusable UI components
│   ├── Loader.tsx
│   ├── Modal.tsx
│   ├── Paginator.tsx
│   └── Pills.tsx
├── features/             # Feature-based modules
│   ├── auth/             # Authentication (Login, Register, ResetPassword)
│   ├── app/              # Main app features
│   │   ├── profile/      # User profile
│   │   ├── users/        # User management (admin)
│   │   ├── view-admin-user/
│   │   ├── view-only-admin/
│   │   └── view-only-user/
│   └── home/
├── hooks/                # Custom hooks
│   └── useTableController.ts
├── layout/               # Layout components
│   ├── GeneralLoader.tsx
│   ├── Header.tsx
│   ├── MainSection.tsx
│   └── SideMenu.tsx
├── locales/              # i18n translations
│   ├── en_GB.json
│   └── es_ES.json
├── providers/            # Context providers
│   └── ReactQueryProvider.tsx
├── router/               # Routing configuration
│   ├── AppRouter.tsx
│   ├── PrivateRoute.tsx
│   └── RoleRoute.tsx
├── types/                # TypeScript types
│   └── paginator.types.ts
├── utils/                # Utilities
│   ├── axios.ts          # Axios instance with interceptors
│   ├── cn.ts             # Class name utility
│   ├── helpers.ts        # Helper functions
│   ├── i18n.ts           # i18n configuration
│   └── toastUtils.ts     # Toast utilities
└── main.tsx              # Entry point
```

## Features

- **Authentication**: Login, Register, Reset Password with JWT
- **Role-based Access Control**: Admin and User roles
- **User Management**: CRUD operations for users (Admin only)
- **Internationalization**: English (en_GB) and Spanish (es_ES)
- **Theme Support**: Light, Dark, and System preferences
- **Auto Logout**: Idle session timeout
- **CSRF Protection**: Token-based security
- **Responsive Design**: Mobile-friendly UI

## Scripts

```bash
npm run dev      # Start development server
npm run build    # Build for production
npm run lint     # Run ESLint
npm run preview  # Preview production build
```

## Environment Variables

Create `.env` file in the root directory:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

## Dependencies

### Core
- react, react-dom (^19.2.0)
- react-router-dom (^7.13.0)

### State & Data
- jotai (^2.17.1)
- @tanstack/react-query (^5.91.3)
- axios (^1.13.5)

### Forms & Validation
- react-hook-form (^7.71.1)
- @hookform/resolvers (^5.2.2)
- zod (^4.3.6)

### UI & Styling
- tailwindcss (^4.1.18)
- @tailwindcss/vite (^4.1.18)
- tailwind-merge (^3.5.0)
- clsx (^2.1.1)
- react-icons (^5.5.0)
- react-toastify (^11.0.5)

### i18n
- i18next (^25.10.3)
- i18next-browser-languagedetector (^8.2.1)
- react-i18next (^16.6.0)

### Utilities
- jwt-decode (^4.0.0)
- query-string (^9.3.1)

## License

MIT