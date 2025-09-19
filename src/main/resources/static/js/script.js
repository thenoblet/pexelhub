// Modal functionality
function openUploadModal() {
  document.getElementById('uploadModal').classList.add('show');
  document.body.style.overflow = 'hidden';
}

function closeUploadModal() {
  document.getElementById('uploadModal').classList.remove('show');
  document.body.style.overflow = 'auto';
  resetUploadForm();
}

// Close modal when clicking outside
document.getElementById('uploadModal').addEventListener('click', function(e) {
  if (e.target === this) {
    closeUploadModal();
  }
});

// File handling
let selectedFiles = [];

document.getElementById('fileInput').addEventListener('change', handleFileSelect);

function handleFileSelect(e) {
  const files = Array.from(e.target.files);
  selectedFiles = [...selectedFiles, ...files];
  displayFilePreview();
}

// Drag and drop functionality
const uploadArea = document.getElementById('uploadArea');

uploadArea.addEventListener('dragover', function(e) {
  e.preventDefault();
  uploadArea.classList.add('dragover');
});

uploadArea.addEventListener('dragleave', function(e) {
  e.preventDefault();
  uploadArea.classList.remove('dragover');
});

uploadArea.addEventListener('drop', function(e) {
  e.preventDefault();
  uploadArea.classList.remove('dragover');

  const files = Array.from(e.dataTransfer.files).filter(file =>
      file.type.startsWith('image/')
  );

  selectedFiles = [...selectedFiles, ...files];
  displayFilePreview();
});

function displayFilePreview() {
  const previewContainer = document.getElementById('filePreview');
  const uploadButton = document.getElementById('uploadButton');

  if (selectedFiles.length === 0) {
    previewContainer.style.display = 'none';
    uploadButton.disabled = true;
    return;
  }

  previewContainer.style.display = 'block';
  uploadButton.disabled = false;

  previewContainer.innerHTML = '';

  selectedFiles.forEach((file, index) => {
    const previewItem = document.createElement('div');
    previewItem.className = 'preview-item';

    const img = document.createElement('img');
    img.className = 'preview-image';
    img.src = URL.createObjectURL(file);

    const info = document.createElement('div');
    info.className = 'preview-info';

    const name = document.createElement('div');
    name.className = 'preview-name';
    name.textContent = file.name;

    const size = document.createElement('div');
    size.className = 'preview-size';
    size.textContent = formatFileSize(file.size);

    const progressBar = document.createElement('div');
    progressBar.className = 'progress-bar';

    const progressFill = document.createElement('div');
    progressFill.className = 'progress-fill';
    progressFill.style.width = '0%';

    progressBar.appendChild(progressFill);

    info.appendChild(name);
    info.appendChild(size);
    info.appendChild(progressBar);

    previewItem.appendChild(img);
    previewItem.appendChild(info);

    previewContainer.appendChild(previewItem);
  });
}

function formatFileSize(bytes) {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

function uploadFiles() {
  if (selectedFiles.length === 0) return;

  const uploadButton = document.getElementById('uploadButton');
  uploadButton.disabled = true;
  uploadButton.textContent = 'Uploading...';

  // Create FormData for S3 upload
  const formData = new FormData();
  selectedFiles.forEach(file => {
    formData.append('file', file);
  });

  fetch('/api/v1/upload', {
    method: 'POST',
    body: formData
  })
      .then(response => {
        if (!response.ok) {
          throw new Error('Upload failed');
        }
        return response.text();
      })
      .then(data => {
        console.log('Upload successful:', data);
        alert('File !');
        closeUploadModal();
        // Refresh the page to show new photos from S3
        window.location.reload();
      })
      .catch(error => {
        console.error('S3 Upload error:', error);
        alert('Upload failed. Please try again.');
        uploadButton.disabled = false;
        uploadButton.textContent = 'Upload';
      });

  // Simulate progress for UI feedback
  selectedFiles.forEach((file, index) => {
    simulateUpload(index);
  });
}

function simulateUpload(index) {
  const progressFill = document.querySelectorAll('.progress-fill')[index];
  let progress = 0;

  const interval = setInterval(() => {
    progress += Math.random() * 15;
    if (progress >= 100) {
      progress = 100;
      clearInterval(interval);
    }
    progressFill.style.width = progress + '%';
  }, 200);
}

function resetUploadForm() {
  selectedFiles = [];
  document.getElementById('fileInput').value = '';
  document.getElementById('filePreview').style.display = 'none';
  document.getElementById('uploadButton').disabled = true;
  document.getElementById('uploadButton').textContent = 'Upload';

  // Clear any existing progress bars
  const progressFills = document.querySelectorAll('.progress-fill');
  progressFills.forEach(fill => fill.style.width = '0%');
}

// See more functionality - integrated with Thymeleaf data
let currentOffset = 5; // Start after the first 5 photos shown by Thymeleaf
let hasMoreImages = true;

// Initialize from Thymeleaf template data
document.addEventListener('DOMContentLoaded', function() {
  // This would be set by your Thymeleaf template
  const totalPhotos = parseInt(document.body.getAttribute('data-total-photos') || '0');
  hasMoreImages = totalPhotos > 5;
});

function loadMoreImages() {
  const seeMoreBtn = document.getElementById('seeMoreBtn');

  if (!hasMoreImages) {
    return; // Do nothing if no more images
  }

  // Show loading state
  seeMoreBtn.textContent = 'Loading...';
  seeMoreBtn.disabled = true;

  // Fetch more images from Spring Boot backend (S3 URLs)
  fetch(`/api/v1/photos/more?offset=${currentOffset}&limit=5`)
      .then(response => {
        if (!response.ok) {
          throw new Error('Failed to load more images from S3');
        }
        return response.json();
      })
      .then(data => {
        if (data.photos && data.photos.length > 0) {
          data.photos.forEach(photo => {
            addPhotoToGrid(photo.s3Url, photo.description || 'Uploaded photo');
          });
          currentOffset += data.photos.length;
          hasMoreImages = data.hasMore;
        } else {
          hasMoreImages = false;
        }
        updateSeeMoreButton();
      })
      .catch(error => {
        console.error('Error loading more images from S3:', error);
        seeMoreBtn.textContent = 'Error loading images';
        setTimeout(() => {
          seeMoreBtn.textContent = 'See more';
          seeMoreBtn.disabled = false;
        }, 2000);
      });
}

function addPhotoToGrid(imageSrc, altText) {
  const photoGrid = document.getElementById('photoGrid');
  const photoItem = document.createElement('div');
  photoItem.className = 'photo-item';

  const img = document.createElement('img');
  img.src = imageSrc;
  img.alt = altText;
  img.loading = 'lazy';

  photoItem.appendChild(img);
  photoGrid.appendChild(photoItem);

  // Add click event to new photo
  photoItem.addEventListener('click', function() {
    console.log('Photo clicked:', imageSrc);
  });
}

function updateSeeMoreButton() {
  const seeMoreBtn = document.getElementById('seeMoreBtn');

  if (hasMoreImages) {
    seeMoreBtn.textContent = 'See more';
    seeMoreBtn.disabled = false;
  } else {
    seeMoreBtn.textContent = 'No more images';
    seeMoreBtn.disabled = true;
    seeMoreBtn.style.opacity = '0.6';
    seeMoreBtn.style.cursor = 'not-allowed';
  }
}

// Photo grid interactions
document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('.photo-item').forEach(item => {
    item.addEventListener('click', function() {
      console.log('Photo clicked');
    });
  });
});