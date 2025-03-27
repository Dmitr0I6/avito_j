import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import axios from "axios";
import { useState } from "react";

const schema = yup.object().shape({
    title: yup.string().required("Название обязательно"),
    description: yup.string().required("Описание обязательно"),
    category: yup.number().required("Категория обязательна").positive(),
    price: yup.number().required("Цена обязательна").positive(),
    userId: yup.number().required("ID пользователя обязательно").positive(),
});

export default function CreateAdvertisementForm() {
    const [files, setFiles] = useState([]);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [submitSuccess, setSubmitSuccess] = useState(false);
    const [error, setError] = useState(null);

    const {
        register,
        handleSubmit,
        formState: { errors },
        reset,
    } = useForm({
        resolver: yupResolver(schema),
    });

    const handleFileChange = (e) => {
        setFiles([...e.target.files]);
    };

    const onSubmit = async (data) => {
        setIsSubmitting(true);
        setError(null);

        try {
            const formData = new FormData();

            // Добавляем поля формы
            formData.append("title", data.title);
            formData.append("description", data.description);
            formData.append("category", data.category);
            formData.append("price", data.price);
            formData.append("userId", data.userId);

            // Добавляем файлы
            files.forEach((file) => {
                formData.append("images", file);
            });

            const response = await axios.post(
                "http://localhost:9000/v1/advertisments",
                formData,
                {
                    headers: {
                        "Content-Type": "multipart/form-data",
                    },
                }
            );

            setSubmitSuccess(true);
            reset();
            setFiles([]);
        } catch (err) {
            setError(err.response?.data?.message || "Ошибка при создании объявления");
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="max-w-md mx-auto p-6 bg-white rounded-lg shadow-md">
            <h2 className="text-2xl font-bold mb-6">Создать новое объявление</h2>

            {submitSuccess && (
                <div className="mb-4 p-4 bg-green-100 text-green-700 rounded">
                    Объявление успешно создано!
                </div>
            )}

            {error && (
                <div className="mb-4 p-4 bg-red-100 text-red-700 rounded">
                    {error}
                </div>
            )}

            <form onSubmit={handleSubmit(onSubmit)}>
                <div className="mb-4">
                    <label className="block text-gray-700 mb-2" htmlFor="title">
                        Название
                    </label>
                    <input
                        id="title"
                        type="text"
                        {...register("title")}
                        className="w-full px-3 py-2 border rounded"
                    />
                    {errors.title && (
                        <p className="text-red-500 text-sm mt-1">{errors.title.message}</p>
                    )}
                </div>

                <div className="mb-4">
                    <label className="block text-gray-700 mb-2" htmlFor="description">
                        Описание
                    </label>
                    <textarea
                        id="description"
                        {...register("description")}
                        className="w-full px-3 py-2 border rounded"
                        rows="4"
                    />
                    {errors.description && (
                        <p className="text-red-500 text-sm mt-1">
                            {errors.description.message}
                        </p>
                    )}
                </div>

                <div className="mb-4">
                    <label className="block text-gray-700 mb-2" htmlFor="category">
                        Категория (ID)
                    </label>
                    <input
                        id="category"
                        type="number"
                        {...register("category")}
                        className="w-full px-3 py-2 border rounded"
                    />
                    {errors.category && (
                        <p className="text-red-500 text-sm mt-1">{errors.category.message}</p>
                    )}
                </div>

                <div className="mb-4">
                    <label className="block text-gray-700 mb-2" htmlFor="price">
                        Цена
                    </label>
                    <input
                        id="price"
                        type="number"
                        step="0.01"
                        {...register("price")}
                        className="w-full px-3 py-2 border rounded"
                    />
                    {errors.price && (
                        <p className="text-red-500 text-sm mt-1">{errors.price.message}</p>
                    )}
                </div>

                <div className="mb-4">
                    <label className="block text-gray-700 mb-2" htmlFor="userId">
                        ID пользователя
                    </label>
                    <input
                        id="userId"
                        type="number"
                        {...register("userId")}
                        className="w-full px-3 py-2 border rounded"
                    />
                    {errors.userId && (
                        <p className="text-red-500 text-sm mt-1">{errors.userId.message}</p>
                    )}
                </div>

                <div className="mb-4">
                    <label className="block text-gray-700 mb-2" htmlFor="images">
                        Изображения
                    </label>
                    <input
                        id="images"
                        type="file"
                        multiple
                        onChange={handleFileChange}
                        className="w-full px-3 py-2 border rounded"
                    />
                </div>

                <button
                    type="submit"
                    disabled={isSubmitting}
                    className="w-full bg-blue-500 text-white py-2 px-4 rounded hover:bg-blue-600 disabled:bg-blue-300"
                >
                    {isSubmitting ? "Отправка..." : "Создать объявление"}
                </button>
            </form>
        </div>
    );
}