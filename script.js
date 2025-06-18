document.addEventListener('DOMContentLoaded', function () {
    const dropbtn = document.querySelector('.dropbtn');
    const dropdownContent = document.querySelector('.dropdown-content');
    const dropdownItems = document.querySelectorAll('.dropdown-content a');

    if (dropbtn && dropdownContent && dropdownItems) {
        dropbtn.addEventListener('click', function (event) {
            event.stopPropagation(); // Prevents the document click listener from immediately hiding the dropdown
            dropdownContent.style.display = dropdownContent.style.display === 'block' ? 'none' : 'block';
        });

        // Close the dropdown if the user clicks outside of it
        document.addEventListener('click', function (event) {
            // If the click is outside the button and outside the dropdown content
            if (!dropbtn.contains(event.target) && !dropdownContent.contains(event.target)) {
                dropdownContent.style.display = 'none';
            }
        });

        // Add click listener to each dropdown item
        dropdownItems.forEach(item => {
            item.addEventListener('click', function (event) {
                console.log(`Menu item '${item.textContent}' clicked`);
                dropdownContent.style.display = 'none'; // Hide dropdown after item click
                // event.preventDefault(); // Uncomment if the links have actual hrefs and you want to prevent navigation
            });
        });

    } else {
        console.error('Dropdown button, content, or items not found');
    }
});
