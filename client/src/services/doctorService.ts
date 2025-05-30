import {
  Doctor,
  DoctorRegisterPayload,
  GetAllDoctorsResponse,
} from "@/utils/types";
import axios from "axios";

export const getDoctors = async ({
  name = "",
  specialization = "",
  sortBy = "firstName",
  page = 0,
  size = 10,
}: {
  name: string;
  specialization?: string;
  sortBy?: string;
  page: number;
  size: number;
}) => {
  try {
    const res = await axios.get<GetAllDoctorsResponse>(
      `${import.meta.env.VITE_BACKENDURL}/api/v1/doctors`,
      {
        params: {
          name,
          specialization,
          sortBy,
          page,
          size,
        },
      }
    );
    return res.data;
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (error) {
    throw new Error("Failed to fetch doctors");
  }
};

export const getSingleDoctor = async (id: number | string | undefined) => {
  try {
    const res = await axios.get<Doctor>(
      `${import.meta.env.VITE_BACKENDURL}/api/v1/doctors/${id}`
    );
    console.log(res);
    return res.data;
  } catch (err) {
    console.log(err);
    throw new Error("Failed to fetch doctors");
  }
};
export const registerDoctor = async (
  token: string | null,
  payload: DoctorRegisterPayload
) => {
  const res = await axios.post(
    `${import.meta.env.VITE_BACKENDURL}
/api/v1/admin/register-doctor`,
    payload,
    {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    }
  );

  return res.data;
};
