import PublicDomainIcon from '@/assets/icons/licenses/public_domain.svg';
import CreativeCommonsShareAlikeIcon from '@/assets/icons/licenses/cc_by_sa.svg';
import CopyrightIcon from '@/assets/icons/licenses/copyright.svg';

export const LICENSES = {
    PUBLIC_DOMAIN: {
        id: 1,
        name: 'Суспільне надбання',
        description: 'Суспільне надбання — це усі фото, термін дії авторських прав яких закінчився зі згоди автора чи через 70 років після його (її) смерті.',
        icon: PublicDomainIcon,
        link: 'https://uk.wikipedia.org/wiki/Суспільне_надбання'
    },
    CREATIVE_COMMONS_SA_BY: {
        id: 2,
        name: 'Creative Commons Attribution-ShareAlike 4.0',
        description: 'Фото буде опубліковано під ліцензією Creative Commons ShareAlike. Це означає, що його можна буде вільно поширювати ' +
            'за умови, що буде вказане ваше авторство.',
        icon: CreativeCommonsShareAlikeIcon,
        link: 'https://creativecommons.org/licenses/by-sa/4.0/deed.uk'
    },
    FAIR_USE: {
        id: 3,
        name: 'Добропорядне використання',
        description: 'Буде застосоване добропорядне використання. За зверненням власників авторських прав фото може бути видалене.',
        icon: CopyrightIcon,
        link: 'https://uk.wikipedia.org/wiki/Вікіпедія:Добропорядне_використання'
    }
}

export const FAMILY_ARCHIVE_SOURCE_NAME = 'Власний архів';
