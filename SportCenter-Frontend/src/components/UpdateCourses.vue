<template>
   <div class="solid-background">
    <!-- Toolbar and search bar -->
    <div class="text-search-bar" :class="{ 'blur-background': isPopupOpen }">
      <div class="text-content" style="text-align: left">
        <h1 class="custom-h1">Manage courses</h1>
        <h3 class="custom-h3">Edit or delete courses from the center.</h3>
      </div>
    </div>

    <!-- Display the UpdateCourseForm component when the icon is clicked -->
    <UpdateCourseForm 
        v-if="showUpdateForm" 
        :course="selectedCourse" 
        @close="closeUpdateCourseForm" 
        style="z-index: 9999; position: absolute; top: 50px; left: 50%; transform: translateX(-50%);"
    />

    <div class="mt-5 " :class="{ 'blur-background': isPopupOpen }"> 
      <div class="row">
        <div
          class="col-md-5 col-lg-3 col-sm-12 mb-5"
          v-for="course in list.courses"
          :key="course.id"
        >
          <div>
            <img :src="course.url" :alt="course.name" class="w-100 h-100" />
          </div>
          <h3>
            <span class="white-heading">{{ course.category }}</span>
          </h3>
          <div>
            <h3>{{ capitalize(course.name) }}</h3>
            <p>{{ course.description }}</p>

            <div class="buttons">
              <!-- Display the pencil icon and bind the click event to openUpdateCourseForm method -->
              <b-icon
                icon="pencil-fill"
                @click="openUpdateCourseForm(course)"
                class="pencil-icon"
              ></b-icon>
              <b-icon
                icon="trash-fill"
                @click="deleteCourse(course.id)"
                class="disapprove"
              ></b-icon>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// Import the UpdateCourseForm component
import UpdateCourseForm from "./UpdateCourseForm.vue";

export default {
  name: "Courses",
  data() {
    return {
      list: [],
      hoveredCardColor: "",
      username: "",
      password: "",
      showUpdateForm: false, // Add a data property to track whether to show the UpdateCourseForm
      selectedCourse: null, // Add a data property to store the selected course
    };
  },
  mounted() {
    this.username = decodeURIComponent(this.$cookies.get('username'));
    this.password = this.$cookies.get('password');
    this.getAllCourses();
  },
  methods: {
    getAllCourses() {
      const requestOptions = {
        method: "GET",
        credentials: "include",
      };

      fetch(
        "http://127.0.0.1:8080/public/courses",
        requestOptions
      )
        .then((response) => {
          if (!response.ok) {
            throw new Error("Network response was not ok");
          }
          return response.json();
        })
        .then((data) => {
          this.list = data;
          console.log(this.list);
        })
        .catch((error) => {
          console.error("Error fetching courses:", error);
        });
    },
    capitalize(str) {
      return str.replace(/\b\w/g, (char) => char.toUpperCase());
    },
    deleteCourse(courseId) {
      const username = decodeURIComponent(this.$cookies.get('username'));
      const password = this.$cookies.get('password');

      const requestOptions = {
        method: "DELETE", 
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
          'Authorization': 'Basic ' + btoa(username + ':' + password),
        },
      };

      fetch(
        `http://127.0.0.1:8080/courses/${courseId}`, 
        requestOptions
      )
        .then((response) => {
          if (!response.ok) {
            throw new Error("Network response was not ok");
          }
          console.log("Course deleted successfully");
          this.getAllCourses(); // Refresh the courses list
        })
        .catch((error) => {
          console.error("Error deleting course:", error);
        });
        },
    openUpdateCourseForm(course) {
        console.log("Opening UpdateCourseForm for course:", course);
        this.selectedCourse = course;
        this.showUpdateForm = true; 
        this.isPopupOpen = true; 
    },
    closeUpdateCourseForm() {
        this.showUpdateForm = false; 
        this.isPopupOpen = false; 
    },
  },
  components: {
    UpdateCourseForm, 
  },
};
</script>

<style scoped>
.pencil-icon {
  color: #CDF563; 
  font-size: 20px;
  cursor: pointer; 
  margin-right: 25px; 
}

.pencil-icon:hover {
  color: #fff; 
}

/* Add styles for the UpdateCourseForm */
.solid-background {
  background-color: var(--color-black);
  height: 100vh;
  width: 70vw;
  overflow: auto;
  margin-left: -30px;
  position: relative; /* Set the position to relative for proper positioning of the absolute element */
}

.custom-h1 {
  color: #ffffff;
  font-size: 35px;
}

.custom-h3 {
  font-size: 20px;
  font-weight: 600;
  color: var(--color-white);
}

.search-input {
  width: 180px;
  height: 35px;
  border: 1px solid #ccc;
  border-radius: 20px;
  background-color: #bfd3f2;
  color: #ffffff;
}

.white-heading {
  font-size: 14px;
  color: var(--color-black);
  background-color: #FFF;
  font-weight: 700;
}

h3 {
  font-size: 20px;
  font-weight: 700;
  color: #ffffff;
}

p {
  font-size: 18px;
  font-weight: 400;
  color: #ffffff;
}

body {
  margin: 0;
  padding: 0;
}

.approve{
  border: 0px;
  background-color: #CDF563;
  color: var(--color-black);
  font-weight: bold;
  border-radius: 20px;
  height: 40px;
  width: 100px;
}

.disapprove {
  color: #EC5545;
  font-size: 20px;
  cursor: pointer; 
  margin-right: 25px; 
}

.disapprove:hover {
  color: #fff; 
}

.blur-background {
  filter: blur(2px);
}

.buttons {
  display: flex;
  justify-content: center;
  align-items:center;
}
</style>