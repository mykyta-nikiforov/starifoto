export function isEmpty(value: string): boolean {
    return value === null || value === undefined || value === '' || value.trim() === '';
}

export function trimAndLowerCase(value: string): string {
    return value.trim().toLowerCase();
}