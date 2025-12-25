<template>
  <div>
    <AnnouncementForm :announcement-id="announcementId" />
  </div>
</template>

<script>
import AnnouncementForm from '@/components/AnnouncementForm.vue'

export default {
  name: 'EditAnnouncement',
  components: {
    AnnouncementForm
  },
  computed: {
    announcementId() {
      return this.$route.params.id
    }
  }
}
</script>