const DATE_FORMATTER = new Intl.DateTimeFormat('uk-UA', {day: '2-digit', month: 'long', year: 'numeric'});
const TIME_FORMATTER = new Intl.DateTimeFormat('uk-UA', {hour: '2-digit', minute: '2-digit', hour12: false});

export function formatToDateTime(originalDateString: string) {
    const date = new Date(originalDateString);
    const formattedDate = removeLeadingZero(DATE_FORMATTER.format(date).replace(' р.', ''));
    const formattedTime = TIME_FORMATTER.format(date);
    return `${formattedDate} ${formattedTime}`;
}


export function formatToDate(originalDateString: string | Date) {
    const date = new Date(originalDateString);
    return removeLeadingZero(DATE_FORMATTER.format(date).replace(' р.', ''));
}

function removeLeadingZero(value: string): string {
    return value.replace(/^0+/, '');
}