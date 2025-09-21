/**
 * Opens the upload modal dialog and disables page scrolling.
 *
 * @function
 * @name openUploadModal
 * @description Displays the upload modal by adding 'show' class and prevents
 * background scrolling by setting body overflow to hidden.
 * @returns {void}
 * @since 1.0.0
 * @example
 * // Open the upload modal
 * openUploadModal();
 */
function openUploadModal() {
    document.getElementById('uploadModal').classList.add('show');
    document.body.style.overflow = 'hidden';
}

/**
 * Closes the upload modal dialog and restores page scrolling.
 *
 * @function
 * @name closeUploadModal
 * @description Hides the upload modal, restores body scrolling, and resets
 * the upload form to its initial state.
 * @returns {void}
 * @since 1.0.0
 * @see {@link resetUploadForm} For form reset implementation
 * @example
 * // Close the upload modal
 * closeUploadModal();
 */
function closeUploadModal() {
    document.getElementById('uploadModal').classList.remove('show');
    document.body.style.overflow = 'auto';
    resetUploadForm();
}


document.getElementById('uploadModal').addEventListener('click', function(e) {
    if (e.target === this) {
        closeUploadModal();
    }
});

// File handling
let selectedFiles = [];

document.getElementById('fileInput').addEventListener('change', handleFileSelect);

/**
 * Handles file selection from the file input element.
 *
 * @function
 * @name handleFileSelect
 * @description Processes files selected via file input, adds them to the
 * selectedFiles array, and triggers the file preview display.
 * @param {Event} e - The change event from the file input element
 * @param {FileList} e.target.files - The selected files
 * @returns {void}
 * @since 1.0.0
 * @see {@link displayFilePreview} For preview rendering
 * @example
 * // Automatically called when files are selected
 * document.getElementById('fileInput').addEventListener('change', handleFileSelect);
 */
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

/**
 * Renders preview interface for selected files with thumbnails and metadata.
 *
 * @function
 * @name displayFilePreview
 * @description Creates a visual preview of selected files including thumbnails,
 * file names, sizes, and progress bars. Updates UI state based on file selection.
 * @returns {void}
 * @since 1.0.0
 * @see {@link formatFileSize} For file size formatting
 * @throws {Error} May throw if file URL creation fails
 * @example
 * // Called automatically after file selection
 * displayFilePreview();
 */
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

/**
 * Converts file size from bytes to human-readable format with appropriate units.
 *
 * @function
 * @name formatFileSize
 * @description Formats byte values into human-readable strings using standard
 * binary units (Bytes, KB, MB, GB) with appropriate decimal precision.
 * @param {number} bytes - The file size in bytes
 * @returns {string} Formatted file size string (e.g., "1.25 MB", "512 KB")
 * @since 1.0.0
 * @example
 * formatFileSize(1024);     // Returns "1 KB"
 * formatFileSize(1536000);  // Returns "1.46 MB"
 * formatFileSize(0);        // Returns "0 Bytes"
 */
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

/**
 * Initiates file upload process to S3 storage via API endpoint.
 *
 * @async
 * @function
 * @name uploadFiles
 * @description Uploads selected files to S3 storage using FormData and fetch API.
 * Provides visual feedback through progress simulation and handles success/error states.
 * @returns {Promise<void>} Resolves when upload completes or fails
 * @throws {Error} Network errors or HTTP response errors
 * @since 1.0.0
 * @see {@link simulateUpload} For progress animation
 * @example
 * // Initiate upload process
 * await uploadFiles();
 */
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

/**
 * Simulates upload progress animation for enhanced user experience.
 *
 * @function
 * @name simulateUpload
 * @description Creates a visual progress animation for file uploads using
 * randomized increments to simulate realistic upload behavior.
 * @param {number} index - Zero-based index of the file in the upload queue
 * @returns {void}
 * @since 1.0.0
 * @example
 * // Simulate progress for the first file
 * simulateUpload(0);
 */
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

/**
 * Resets the upload form to its initial empty state.
 *
 * @function
 * @name resetUploadForm
 * @description Clears selected files array, resets file input, hides preview
 * container, and restores upload button to default state.
 * @returns {void}
 * @since 1.0.0
 * @example
 * // Reset form after upload or cancellation
 * resetUploadForm();
 */
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

/**
 * Loads additional images from the server with pagination support.
 *
 * @async
 * @function
 * @name loadMoreImages
 * @description Fetches additional photos from the backend API using offset-based
 * pagination. Updates the photo grid and manages loading states.
 * @returns {Promise<void>} Resolves when images are loaded or request fails
 * @throws {Error} Network errors or API response errors
 * @since 1.0.0
 * @see {@link addPhotoToGrid} For adding photos to display
 * @see {@link updateSeeMoreButton} For button state management
 * @example
 * // Load next batch of images
 * await loadMoreImages();
 */
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

/**
 * Dynamically adds a new photo element to the photo grid.
 *
 * @function
 * @name addPhotoToGrid
 * @description Creates and appends a new photo item to the grid with lazy loading
 * and click event handling. Maintains consistent styling and behavior.
 * @param {string} imageSrc - The URL of the image to display
 * @param {string} altText - Alternative text for accessibility
 * @returns {void}
 * @since 1.0.0
 * @example
 * // Add a new photo to the grid
 * addPhotoToGrid('https://example.com/photo.jpg', 'Vacation photo');
 */
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

/**
 * Updates the "See more" button state based on available content.
 *
 * @function
 * @name updateSeeMoreButton
 * @description Manages the visual state and functionality of the pagination button
 * based on whether more images are available to load.
 * @returns {void}
 * @since 1.0.0
 * @example
 * // Update button after loading images
 * updateSeeMoreButton();
 */
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