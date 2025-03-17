// This is a simple regex matching the markdown link syntax [text](url)
const markdownLinkPattern = /\[([^\]]+)\]\(<(https[^>]+)>\)/g;

export function parseMarkupToHtml(markup: string): string {
    return markup.replace(markdownLinkPattern, '<a href="$2" target="_blank" rel="noopener noreferrer">$1</a>');
}

export function convertUrlToMarkdownLink(url: string, text: string): string {
    return `[${text}](<${url}>)`;
}

export function convertUrlToHtmlLink(url: string): string {
    const domain = new URL(url).hostname;
    return `<a href="${url}" target="_blank" rel="noopener noreferrer">${domain}</a>`;
}

export function removeLinksFromText(text: string): string {
    if (!text) {
        return text;
    }
    return text.replace(markdownLinkPattern, '$1');
}