// src/stores/file.js
import { defineStore } from 'pinia';
import axios from 'axios';

function extractFileName(filePath) {
  if (!filePath) return '';
  const parts = filePath.split('\\'); // Windows 경로 구분자
  return parts[parts.length - 1];
}

export const useFileStore = defineStore('file', {
  state: () => ({
    uploadedFiles: [],
    loading: false,
    error: null,
  }),

  actions: {
    async uploadFiles({ files, referenceTable, referenceNo = -1 }) {
      this.loading = true;
      this.error = null;
      const uploaded = [];

      try {
        for (const file of files) {
          const formData = new FormData();
          formData.append('file', file);
          formData.append('referenceTable', referenceTable);
          formData.append('referenceNo', referenceNo); // 상품이 아직 없으면 -1
          formData.append('fileName', file.name);

          const res = await axios.post('/v1/file', formData, {
            headers: {
              'Content-Type': 'multipart/form-data',
            },
          });
          
          const fileData = res.data;

          if (!fileData.imageUrl && fileData.filePath) {
              const fileName = extractFileName(fileData.filePath);
              fileData.imageUrl = `/files/${fileName}`; // ✅ 경로 포함하여 다시 생성
            }

          uploaded.push(res.data); // { fileNo, imageUrl, ... }
        }

        this.uploadedFiles = uploaded;
        return uploaded;
      } catch (err) {
        this.error = err;
        throw err;
      } finally {
        this.loading = false;
      }
    },
  },
});
