<template>
  <div class="solid-background">
    <div class="box">
      <div class="form-box">
        <div class="title-container">
          <h3 class="custom-h3">Edit session details below</h3>
        </div>
        <form @submit.prevent="submitForm">
          <div class="form-group">
            <label>Capacity</label>
            <input class="text-field" v-model="session.capacity" required>
          </div>
          <div class="form-group flex-container">
            <div class="form-group">
              <label>Date</label>
              <input class="text-field" type="date" v-model="session.date" required>
            </div>
            <div class="starttime">
              <label>Start Time</label>
              <input class="text-field" type="time" v-model="session.startTime" required>
            </div>
            <div class="endtime">
              <label>End Time</label>
              <input class="text-field" type="time" v-model="session.endTime" required>
            </div>
          </div>
          <div class="form-group">
            <label>Floor</label>
            <div class="choice">
              <select v-model="session.location.id" class="text-field">
               <option v-for="location in locations.locations" :key="location.id" :value="location.id">{{ location.floor }}</option>
              </select>
            </div>
            <label>Room</label>
            <div class="choice">
              <select v-model="session.location.id" class="text-field">
             <option v-for="location in locations.locations" :key="location.id" :value="location.id">{{ location.room }}</option>
              </select>
            </div>
          </div>
          <div class="buttons">
            <button id="save-btn" type="submit">Save</button>
            <button id="cancel-btn" type="button" @click="cancelForm">Cancel</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      sessionID: "", // Initialize sessionID
      courses: [], // Array to store the list of courses
      locations: []
    };
  },
  props: {
    session: Object,
  },
  mounted() {
    // Fetch the list of courses and locations when the component is mounted
    this.getAllCourses();
    this.getAllLocations();
  },
  methods: {
    submitForm() {
  const formatTimeWithSeconds = (timeString) => {
    const timeParts = timeString.split(':');
    // Ensure that all time parts (hours, minutes, seconds) are present
    const formattedTime = `${timeParts[0]}:${timeParts[1]}:${timeParts[2] || '00'}`;
    return formattedTime;
  };

  const username = decodeURIComponent(this.$cookies.get('username'));
  const password = this.$cookies.get('password');

  console.log('Username:', username);
  console.log('Password:', password);

  if (username && password) {

    console.log(this.session.course.id);

    fetch(`http://127.0.0.1:8080/courses?name=${this.session.course.name}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Basic ' + btoa(username + ':' + password)
      },
      credentials: 'include',
    })
      .then(response => {
        console.log('Course Getting Response Status:', response.status);
        return response.json();
      })
      .then(data => {
        console.log('Course got:', data);
        this.getCourse = data;

        // Fetch supervisor data
        fetch(`http://127.0.0.1:8080/instructors/${this.$cookies.get('id')}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Basic ' + btoa(username + ':' + password)
          },
          credentials: 'include',
        })
          .then(response => {
            console.log('Instructor getting:', response.status);
            return response.json();
          })
          .then(data => {
            console.log('Instructor got:', data);
            this.getInstructor = data;

            const sessionCreate = {
              capacity: this.session.capacity,
              date: this.session.date,
              course: {
                name: this.getCourse.name,
                difficulty: this.getCourse.difficulty,
                status: this.getCourse.status,
                description: this.getCourse.description,
                pricePerHour: this.getCourse.pricePerHour,
                category: this.getCourse.category,
                url: this.getCourse.url,
              },
              supervisor: {
                name: this.getInstructor.name,
                email: this.getInstructor.email,
                password: this.$cookies.get('password'), // Corrected interpolation
                imageURL: this.getInstructor.imageURL,
                pronouns: this.getInstructor.pronouns
              },
              location: { floor: this.session.location.floor, room: this.session.location.room }, // Include location
              endTime: formatTimeWithSeconds(this.session.endTime),
              startTime: formatTimeWithSeconds(this.session.startTime),
            };

            console.log(sessionCreate);

            // PUT request to update session
            fetch(`http://127.0.0.1:8080/sessions/${this.session.id}`, {
              method: 'PUT',
              headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Basic ' + btoa(username + ':' + password)
              },
              credentials: 'include',
              body: JSON.stringify(sessionCreate)
            })
              .then(response => {
                console.log('Session Update Response Status:', response.status);
                if (!response.ok) {
                  throw new Error('Network response was not ok');
                }
                return response.json();
              })
              .then(data => {
                console.log('Session updated:', data);
                this.$emit('close');
              })
              .catch(error => {
                console.error('Error updating session:', error);
              });
          })
          .catch(error => {
            console.error('Error getting location:', error);
          });
      })
      .catch(error => {
        console.error('Error getting instructor:', error);
      });
  } else {
    console.error('Username or password is not available');
  }
},
    cancelForm() {
      this.$emit('close');
    },
    getAllCourses() {
      const requestOptions = {
        method: 'GET',
        credentials: 'include'
      };

      fetch('http://127.0.0.1:8080/public/courses?status=Approved', requestOptions)
        .then(response => {
          if (!response.ok) {
            throw new Error('Network response was not ok');
          }
          return response.json();
        })
        .then(data => {
          this.courses = data;
          console.log('Courses:', this.courses);
        })
        .catch(error => {
          console.error('Error fetching courses:', error);
        });
    },
    getAllLocations() {
      const requestOptions = {
        method: 'GET',
        credentials: 'include'
      };

      fetch('http://127.0.0.1:8080/public/locations', requestOptions)
        .then(response => {
          if (!response.ok) {
            throw new Error('Network response was not ok');
          }
          return response.json();
        })
        .then(data => {
          this.locations = data;
          console.log('Locations:', this.locations);
        })
        .catch(error => {
          console.error('Error fetching locations:', error);
        });
    },
  },
};
</script>


<style scoped>
.solid-background {
  background-color: transparent;
  height: 100vh;
}

.box {
  display: flex;
  justify-content: center; 
  align-items: center; 
}

.custom-h3 {
  font-size: 24px;
  font-weight: 700;
  color: #FFFFFF;
}

.form-box {
  width: 700px;
  padding: 50px;
  border: 2px solid var(--color-muted-muted-black);
  border-radius: 20px;
  margin-top: 20px;
  background-color: var(--color-black);
}

.form-group {
  margin-bottom: 15px;
}

.choice {
    width: 100%;
}

label {
  text-align: left;
  display: block;
  margin-bottom: 5px;
  color: #ffffff;
  font-size: 18px;
  font-weight: 500;
}

.text-field,
.text-field-description {
  width: 100%;
  padding: 10px;
  border-radius: 5px;
  border: 1px solid #444;
  background-color: transparent;
  color: #ffffff;
  height: 50px;
  box-sizing: border-box;
  margin-top: 4px;
  transition: border-color 0.1s ease-in-out, font-weight 0.1s ease-in-out, border-width 0.1s ease-in-out;
  font-weight: normal;
  border-width: 1px;
}

.text-field-description {
  height: 100px;
}

.text-field:focus,
.text-field:hover,
.text-field-description:focus,
.text-field-description:hover {
  outline: none;
  border-color: #fff;
}

.text-field:focus,
.text-field-description:focus {
  border-width: 2px;
}

.buttons {
  display: flex;
  justify-content: center; 
}

#save-btn {
  background-color: #CDF563;
}

#cancel-btn {
  margin-left: 75px; 
  background-color: #EC5545;
}

button {
  padding: 10px 10px;
  color: var(--color-black);
  border: none;
  border-radius: 30px;
  cursor: pointer;
  font-size: 18px;
  font-weight: 700;
  width: 100px; 
  height: 50px; 
  margin-bottom: 15px;
}

button:hover {
  background-color: #0056b3;
}

.flex-container {
  display: flex;
  justify-content: space-between;
  align-items: center; 
}

.starttime,
.endtime {
  width: 48%; 
}
</style>