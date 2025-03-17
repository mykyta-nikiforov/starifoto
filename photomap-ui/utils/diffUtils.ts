export function areEqualSets<T>(set1: Set<T>, set2: Set<T>): boolean {
    return set1.size === set2.size
        && [...set1].every(value => set2.has(value));
}

export function areEqualArrays<T>(array1: T[], array2: T[]): boolean {
    return array1.length === array2.length
        && array1.every((value, index) => value === array2[index]);
}