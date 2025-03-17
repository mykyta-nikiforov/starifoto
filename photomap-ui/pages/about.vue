<script setup lang="ts">
import GeneralViewWrapper from "@/components/view/GeneralViewWrapper.vue";
import RedLinedHeader2 from "@/components/common/RedLinedHeader2.vue";
import PublicDomainIcon from '../assets/icons/licenses/public_domain.svg';
import CreativeCommonsShareAlikeIcon from '../assets/icons/licenses/cc_by_sa.svg';
import CopyrightIcon from '../assets/icons/licenses/copyright.svg';
import {LICENSES} from "../constants/licenses";

useSeoMeta
({
  description: 'Інформація про проєкт, як долучитися та які фото можна завантажувати'
});

useHead({
  title: 'Про проєкт',
});
</script>

<template>
  <GeneralViewWrapper ref="viewWrapper">
    <div class="text-page-wrapper">
      <h1>Про проєкт</h1>
      <p><b>«СтаріФото.Укр»</b> — краудсорсингова платформа, метою якої є зібрати якомога більше старих фотографій
        України
        та зробити їх доступними і зручними для пошуку. Основна ідея — аби можна було зручно на карті подивитися старі
        фото свого міста або села і при цьому відфільтрувати їх за роками або тегами.</p>

      <p>Сайт запущений у березні 2024 року як продовження
        <a href="https://akkermanika.org/map" target="_blank">«Карти фотографій»</a>
        проєкту «Аккерманіка» — онлайн-енциклопедії Білгород-Дністровщини. «Аккерманська» карта з'явилася у вересні 2021
        року.</p>

      <RedLinedHeader2>Що значить краудсорсингова платформа?</RedLinedHeader2>
      <p>Це означає, що ви можете долучитися до проєкту, завантажуючи фото з вашого сімейного архіву або знайдені в
        інтернеті, в яких вже минули авторські права.
      </p>

      <p>Ви можете завантажити фото, додати до них опис, вказати роки, місце та теги. Це допоможе
        іншим користувачам знайти ці фото на карті.</p>

      <RedLinedHeader2>Як долучитися?</RedLinedHeader2>
      <p>Для того щоб завантажити фото, вам потрібно
        <NuxtLink :to="{name: 'login'}">зареєструватися</NuxtLink>
        на сайті. Після цього ви зможете завантажувати
        фото
        та додавати їх на карту.
      </p>

      <RedLinedHeader2>Які фото можна завантажувати?</RedLinedHeader2>
      <p>Зроблені до 2000 року (умовна дата, що поділяє старий та новий час). Фото мають бути з території України. Це
        можуть бути
        фото міст, сіл, пам'яток архітектури, пейзажів, подій, людей тощо. Окрім цього, можливі фотографії з територій,
        історично
        населених українцями, що зображають українську культуру. </p>

      <p><b>Важливо:</b> кожна світлина мусить мати джерело і вказану ліцензію. Дозволені варіанти ліцензій:
        <ul class="licenses-list">
          <li class="license">
            <a :href="LICENSES.PUBLIC_DOMAIN.link" target="_blank">
              <img :src="PublicDomainIcon" alt="Public domain icon" width="20" height="20">
            </a>
            <span><a :href="LICENSES.PUBLIC_DOMAIN.link" target="_blank">Суспільне надбання</a> — це фото, в яких минув термін дії авторських прав: 70 років після смерті автора або
              після публікації, якщо автор невідомий.</span>
          </li>
          <li class="license">
            <a :href="LICENSES.CREATIVE_COMMONS_SA_BY.link" target="_blank">
              <img :src="CreativeCommonsShareAlikeIcon" alt="Creative Commons Share Alike icon"
                   width="20" height="20">
            </a>
            <span><a :href="LICENSES.CREATIVE_COMMONS_SA_BY.link" target="_blank">Creative Commons</a> — фото під відкритою ліцензією.</span></li>
          <li class="license">
            <a :href="LICENSES.FAIR_USE.link" target="_blank">
              <img :src="CopyrightIcon" alt="Copyright icon" width="20" height="20">
            </a>

            <span><a :href="LICENSES.FAIR_USE.link" target="_blank">Добропорядне використання</a> — зображення, в яких ще не минув термін дії авторських прав, але яким немає
            альтернативи і які несуть історичну цінність. Оскільки «СтаріФото.Укр» — освітній сайт, як і на Вікіпедії,
            такий тип зображень прийнятний. При цьому за проханням автора або правовласника фото може бути видалено.
              </span>
          </li>
        </ul>
      </p>
    </div>
  </GeneralViewWrapper>
</template>

<style scoped lang="less">
.licenses-list {
  margin-top: 1rem;
  list-style-type: none;

  .license {
    margin-top: 0.5rem;
    display: flex;
    gap: 1rem;
    align-items: start;

    img {
      margin-top: 0.25rem;
    }

    a:link, a:visited {
      color: #000;
    }
  }
}
</style>