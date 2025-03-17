function findAndDecodeCsrfCookie(): string | null {
    const cookie = document.cookie.split('; ').find(row => row.startsWith('XSRF-TOKEN='));
    return cookie ? decodeURIComponent(cookie.split('=')[1]) : null;
}

export async function getCsrfToken(): Promise<string | null> {
    const existingToken = findAndDecodeCsrfCookie();
    if (existingToken) {
        return existingToken;
    }
    
    // Fetch new CSRF token if not present
    try {
        const nuxtApp = useNuxtApp();
        await nuxtApp.$axiosUnauthenticated.get('/csrf');
        // After fetch, check cookies again
        return findAndDecodeCsrfCookie();
    } catch (error) {
        console.error('Failed to fetch CSRF token:', error);
        return null;
    }
}