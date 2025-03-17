export const VALIDATION_RULES = {
    REQUIRED: (v: any) => !!v || 'Це обов\'язкове поле',
    REQUIRED_ARRAY: (v: any[]) => !!v && v.length > 0 || 'Це обов\'язкове поле',
    MAX_CHARS_255: (v: string) => (!v || v && v.length <= 255) || 'Максимальна довжина — 255 символів',
    MIN_CHARS_8: (v: string) => (!v || v && v.length >= 8) || 'Мінімальна довжина — 8 символів',
    MAX_CHARS_2048: (v: string) => (!v || v && v.length <= 2048) || 'Максимальна довжина — 2048 символів',
    EMAIL: (v: string) => !v || /^\w+([.+-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(v) || 'Введіть коректний email',
}