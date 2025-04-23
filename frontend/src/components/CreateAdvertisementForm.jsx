import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import axios from "axios";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

const schema = yup.object().shape({
    title: yup.string().required("Название обязательно"),
    description: yup.string().required("Описание обязательно"),
    categoryId: yup.number()
        .typeError('Категория должна быть числом')
        .required("Категория обязательна")
        .positive(),
    price: yup.number()
        .typeError('Цена должна быть числом')
        .required("Цена обязательна")
        .positive(),
});

export default function CreateAdvertisementForm() {
    const navigate = useNavigate();
    const [files, setFiles] = useState([]);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [submitSuccess, setSubmitSuccess] = useState(false);
    const [error, setError] = useState(null);
    const [categories, setCategories] = useState([]);
    const [selectedParentCategory, setSelectedParentCategory] = useState(null);
    const [currentUser, setCurrentUser] = useState(null);

    const {
        register,
        handleSubmit,
        formState: { errors },
        reset,
        setValue,
    } = useForm({
        resolver: yupResolver(schema),
    });

    useEffect(() => {
        // Проверка авторизации пользователя
        const token = localStorage.getItem('accessToken');
        const userId = localStorage.getItem('userId');

        if (!token || !userId) {
            navigate('/login');
            return;
        }

        setCurrentUser({ id: userId });

        // Загрузка категорий
        const fetchCategories = async () => {
            try {
                const response = await axios.get('http://localhost:9000/api/category/all', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                setCategories(response.data);
            } catch (err) {
                console.error('Ошибка при загрузке категорий:', err);
                setError('Не удалось загрузить категории');
            }
        };

        fetchCategories();
    }, [navigate]);

    const handleFileChange = (e) => {
        setFiles([...e.target.files]);
    };

    const handleParentCategoryChange = (e) => {
        const parentId = parseInt(e.target.value);
        setSelectedParentCategory(parentId === 0 ? null : parentId);
        setValue("categoryId", "");
    };

    const onSubmit = async (data) => {
        if (!currentUser) {
            setError("Пользователь не авторизован");
            return;
        }

        setIsSubmitting(true);
        setError(null);

        try {

            const token = localStorage.getItem('accessToken');
            console.log(token);

            const formData = new FormData();
            formData.append("title", data.title);
            formData.append("description", data.description);
            formData.append("categoryId", Number(data.categoryId));
            formData.append("price", data.price);
            formData.append("userId", currentUser.id);

            files.forEach((file) => {
                formData.append("images", file);
            });

            console.log("FormData contents:");
            for (let [key, value] of formData.entries()) {
                console.log(key, value instanceof File ?
                    `File: ${value.name}, size: ${value.size} bytes` :
                    value);
            }

            await axios.post("http://localhost:9000/api/advertisement/createad", formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                    "Authorization": `Bearer ${token}`
                },
            });

            setSubmitSuccess(true);
            reset();
            setFiles([]);
            setTimeout(() => setSubmitSuccess(false), 3000);
        } catch (err) {
            if (err.response?.status === 401) {
                setError("Сессия истекла. Пожалуйста, войдите снова.");
                localStorage.removeItem('accessToken');
                localStorage.removeItem('userId');
                navigate('/login');
            } else {
                setError(err.response?.data?.message || "Ошибка при создании объявления");
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    // Группируем категории
    const parentCategories = categories.filter(cat => cat.parentCategoryId === 0);
    const childCategories = selectedParentCategory
        ? categories.filter(cat => cat.parentCategoryId === selectedParentCategory)
        : [];

    return (
        <div style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            minHeight: '100vh',
            width: '100vw',
            backgroundColor: '#f5f7fa',
            padding: '20px'
        }}>
            <div style={{
                width: '100%',
                maxWidth: '500px',
                padding: '40px',
                backgroundColor: 'white',
                borderRadius: '12px',
                boxShadow: '0 5px 15px rgba(0, 0, 0, 0.08)',
                margin: '0 auto'
            }}>
                <div style={{
                    textAlign: 'center',
                    marginBottom: '30px'
                }}>
                    <h2 style={{
                        fontSize: '28px',
                        fontWeight: '600',
                        color: '#2d3748',
                        marginBottom: '10px'
                    }}>
                        Создать новое объявление
                    </h2>
                    <p style={{
                        color: '#718096',
                        fontSize: '16px'
                    }}>
                        Заполните все поля для публикации объявления
                    </p>
                </div>

                {submitSuccess && (
                    <div style={{
                        padding: '15px',
                        backgroundColor: '#f0fdf4',
                        color: '#16a34a',
                        borderRadius: '8px',
                        marginBottom: '25px',
                        textAlign: 'center',
                        fontSize: '15px'
                    }}>
                        Объявление успешно создано!
                    </div>
                )}

                {error && (
                    <div style={{
                        padding: '15px',
                        backgroundColor: '#fee2e2',
                        color: '#dc2626',
                        borderRadius: '8px',
                        marginBottom: '25px',
                        textAlign: 'center',
                        fontSize: '15px'
                    }}>
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit(onSubmit)}>
                    <div style={{
                        display: 'grid',
                        gridTemplateColumns: '1fr 1fr',
                        gap: '20px',
                        marginBottom: '20px'
                    }}>
                        <div style={{ gridColumn: 'span 2' }}>
                            <label style={{
                                display: 'block',
                                marginBottom: '8px',
                                fontWeight: '500',
                                color: '#4a5568',
                                fontSize: '15px'
                            }}>
                                Название объявления
                            </label>
                            <input
                                type="text"
                                {...register("title")}
                                style={{
                                    width: '100%',
                                    padding: '14px',
                                    border: '1px solid #e2e8f0',
                                    borderRadius: '8px',
                                    fontSize: '15px',
                                    boxSizing: 'border-box',
                                    transition: 'border-color 0.2s',
                                    outline: 'none'
                                }}
                                onFocus={(e) => e.target.style.borderColor = '#3182ce'}
                                onBlur={(e) => e.target.style.borderColor = '#e2e8f0'}
                            />
                            {errors.title && (
                                <p style={{ color: '#dc2626', fontSize: '14px', marginTop: '4px' }}>
                                    {errors.title.message}
                                </p>
                            )}
                        </div>

                        <div style={{ gridColumn: 'span 2' }}>
                            <label style={{
                                display: 'block',
                                marginBottom: '8px',
                                fontWeight: '500',
                                color: '#4a5568',
                                fontSize: '15px'
                            }}>
                                Описание
                            </label>
                            <textarea
                                {...register("description")}
                                style={{
                                    width: '100%',
                                    padding: '14px',
                                    border: '1px solid #e2e8f0',
                                    borderRadius: '8px',
                                    fontSize: '15px',
                                    boxSizing: 'border-box',
                                    transition: 'border-color 0.2s',
                                    outline: 'none',
                                    minHeight: '120px'
                                }}
                                onFocus={(e) => e.target.style.borderColor = '#3182ce'}
                                onBlur={(e) => e.target.style.borderColor = '#e2e8f0'}
                            />
                            {errors.description && (
                                <p style={{ color: '#dc2626', fontSize: '14px', marginTop: '4px' }}>
                                    {errors.description.message}
                                </p>
                            )}
                        </div>

                        <div style={{ gridColumn: 'span 2' }}>
                            <label style={{
                                display: 'block',
                                marginBottom: '8px',
                                fontWeight: '500',
                                color: '#4a5568',
                                fontSize: '15px'
                            }}>
                                Родительская категория
                            </label>
                            <select
                                onChange={handleParentCategoryChange}
                                style={{
                                    width: '100%',
                                    padding: '14px',
                                    border: '1px solid #e2e8f0',
                                    borderRadius: '8px',
                                    fontSize: '15px',
                                    boxSizing: 'border-box',
                                    transition: 'border-color 0.2s',
                                    outline: 'none'
                                }}
                            >
                                <option value="0">Выберите категорию</option>
                                {parentCategories.map((category) => (
                                    <option key={category.id} value={category.id}>
                                        {category.categoryName}
                                    </option>
                                ))}
                            </select>
                        </div>

                        {selectedParentCategory && (
                            <div style={{ gridColumn: 'span 2' }}>
                                <label style={{
                                    display: 'block',
                                    marginBottom: '8px',
                                    fontWeight: '500',
                                    color: '#4a5568',
                                    fontSize: '15px'
                                }}>
                                    Подкатегория
                                </label>
                                <select
                                    {...register("categoryId")}
                                    style={{
                                        width: '100%',
                                        padding: '14px',
                                        border: '1px solid #e2e8f0',
                                        borderRadius: '8px',
                                        fontSize: '15px',
                                        boxSizing: 'border-box',
                                        transition: 'border-color 0.2s',
                                        outline: 'none'
                                    }}
                                >
                                    <option value="">Выберите подкатегорию</option>
                                    {childCategories.map((category) => (
                                        <option key={category.id} value={category.id}>
                                            {category.categoryName}
                                        </option>
                                    ))}
                                </select>
                                {errors.categoryId && (
                                    <p style={{ color: '#dc2626', fontSize: '14px', marginTop: '4px' }}>
                                        {errors.categoryId.message}
                                    </p>
                                )}
                            </div>
                        )}

                        <div>
                            <label style={{
                                display: 'block',
                                marginBottom: '8px',
                                fontWeight: '500',
                                color: '#4a5568',
                                fontSize: '15px'
                            }}>
                                Цена
                            </label>
                            <input
                                type="number"
                                step="0.01"
                                {...register("price")}
                                style={{
                                    width: '100%',
                                    padding: '14px',
                                    border: '1px solid #e2e8f0',
                                    borderRadius: '8px',
                                    fontSize: '15px',
                                    boxSizing: 'border-box',
                                    transition: 'border-color 0.2s',
                                    outline: 'none'
                                }}
                                onFocus={(e) => e.target.style.borderColor = '#3182ce'}
                                onBlur={(e) => e.target.style.borderColor = '#e2e8f0'}
                            />
                            {errors.price && (
                                <p style={{ color: '#dc2626', fontSize: '14px', marginTop: '4px' }}>
                                    {errors.price.message}
                                </p>
                            )}
                        </div>

                        <div style={{ gridColumn: 'span 2' }}>
                            <label style={{
                                display: 'block',
                                marginBottom: '8px',
                                fontWeight: '500',
                                color: '#4a5568',
                                fontSize: '15px'
                            }}>
                                Изображения (максимум 5)
                            </label>
                            <input
                                type="file"
                                multiple
                                onChange={handleFileChange}
                                accept="image/*"
                                max={5}
                                style={{
                                    width: '100%',
                                    padding: '14px',
                                    border: '1px solid #e2e8f0',
                                    borderRadius: '8px',
                                    fontSize: '15px',
                                    boxSizing: 'border-box',
                                    transition: 'border-color 0.2s',
                                    outline: 'none'
                                }}
                            />
                            {files.length > 0 && (
                                <p style={{ color: '#4a5568', fontSize: '14px', marginTop: '8px' }}>
                                    Выбрано файлов: {files.length}
                                </p>
                            )}
                        </div>
                    </div>

                    <button
                        type="submit"
                        disabled={isSubmitting}
                        style={{
                            width: '100%',
                            padding: '16px',
                            backgroundColor: isSubmitting ? '#a0aec0' : '#4299e1',
                            color: 'white',
                            border: 'none',
                            borderRadius: '8px',
                            fontSize: '16px',
                            fontWeight: '600',
                            cursor: isSubmitting ? 'not-allowed' : 'pointer',
                            marginTop: '15px',
                            transition: 'background-color 0.2s, transform 0.1s'
                        }}
                    >
                        {isSubmitting ? 'Отправка...' : 'Создать объявление'}
                    </button>
                </form>
            </div>
        </div>
    );
}