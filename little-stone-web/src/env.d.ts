interface ImportMetaEnv {
    readonly VITE_APP_TITLE: string
    readonly VITE_API_BASE_URL: string
    readonly ENCRYPT_SECRET_KEY: string
    readonly ENCRYPT_SALT: string
}

interface ImportMeta {
    readonly env: ImportMetaEnv;
}
